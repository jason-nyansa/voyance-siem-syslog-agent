package com.nyansa.siem.api.adapters;

/*-
 * #%L
 * VoyanceSiemSyslogAgent
 * %%
 * Copyright (C) 2019 - 2021 Nyansa, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.closedloupe.protobuf.Api;
import com.closedloupe.protobuf.Common;
import com.nyansa.apis.v1.AnnotationEvent;
import com.nyansa.apis.v1.AnnotationEventInfo;
import com.nyansa.siem.api.ApiPaginatedFetch;
import com.nyansa.siem.api.models.VcoEnterpriseEvent;
import com.nyansa.siem.util.ConfigProperties;
import com.nyansa.siem.util.SyslogLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import static com.nyansa.siem.util.ConfigProperties.configProperties;
import static com.nyansa.siem.util.JsonUtil.jsonUtil;

public class ApiZmqJsonAdapter implements ApiOutputAdapter {
  private static final Logger logger = LogManager.getLogger(SyslogLogger.class);

  private static final int zmqIoThreads = 2;
  private static final int zmqLinger = 1;
  private static final int zmqHWM = 100;
  private static final int zmqHdrSize = 12;
  private static final int apiVersion = 1;

  private ConfigProperties configProps;
  private ZContext zmqContext;
  private ZMQ.Socket zmqSocket;
  private AtomicInteger atomicSeqNo;
  private int companyId;

  public ApiZmqJsonAdapter() {
    this(null);
  }

  public ApiZmqJsonAdapter(ConfigProperties inConfigProps) {
    if (inConfigProps == null) {
      inConfigProps = configProperties();
    }
    configProps = inConfigProps;
    zmqContext = new ZContext(zmqIoThreads);
    zmqSocket = zmqContext.createSocket(SocketType.PUSH);
    zmqSocket.setLinger(zmqLinger);
    zmqSocket.setHWM(zmqHWM);
    zmqSocket.connect("tcp://" + configProps.getZmqHost() + ":" + configProps.getZmqPort());
    atomicSeqNo = new AtomicInteger(0);
    companyId = configProps.getCompanyId();
  }

  public void close() {
    zmqSocket.close();
    zmqContext.close();
  }

  @Override
  public <E> boolean processOne(ApiPaginatedFetch<E, ?> apiFetch, E elem) {
    final VcoEnterpriseEvent vcoEvent = (VcoEnterpriseEvent) elem;
    final AnnotationEventInfo annoEvent = new AnnotationEventInfo();
    annoEvent.setVendor("velocloud");
    annoEvent.setEntityId(vcoEvent.getEdgeName());
    annoEvent.setEntityName(vcoEvent.getEdgeName());
    annoEvent.setEntityType(vcoEvent.getCategory());
    annoEvent.setEventType(vcoEvent.getEvent());
    annoEvent.setEventMessage(vcoEvent.getMessage());
    annoEvent.setEventDetail(vcoEvent.getDetail());
    annoEvent.setEventSubFilterType("segment");
    annoEvent.setEventSubFilterValue(vcoEvent.getSegmentName());
    annoEvent.setTimestamp(vcoEvent.getEventTime().getTime());

    final AnnotationEvent annoEventWrapper = new AnnotationEvent();
    annoEventWrapper.setAnnotationEventInfos(Collections.singletonList(annoEvent));

    final Api.ApiMessage.Builder apiMsgBuilder = Api.ApiMessage.newBuilder();
    apiMsgBuilder.setApiVersion(apiVersion);
    apiMsgBuilder.setApiName("annotationevent");
    apiMsgBuilder.setTimestamp(System.currentTimeMillis());
    apiMsgBuilder.setJson(jsonUtil().dump(annoEventWrapper));

    final Api.ApiMessages.Builder apiMsgsBuilder = Api.ApiMessages.newBuilder();
    apiMsgsBuilder.setCompanyId(companyId);
    apiMsgsBuilder.setCollectorGuid("");
    apiMsgsBuilder.addMessages(apiMsgBuilder);
    final byte[] payload = apiMsgsBuilder.build().toByteArray();


    final boolean headerStatus = zmqSocket.send(zmqHeader(), ZMQ.NOBLOCK | ZMQ.SNDMORE);
    final boolean payloadStatus = zmqSocket.send(payload, ZMQ.NOBLOCK);

    logger.trace("Sent Protobuf via ZMQ: " + apiMsgsBuilder.toString());

    return headerStatus && payloadStatus;
  }

  private byte[] zmqHeader() {
    final byte msgType = Common.MgrMsgType.Api_VALUE;
    final byte flags = 0;
    final short seqNo = (short)(atomicSeqNo.getAndIncrement() % 65536);
    final int crawlerId = 0;

    final ByteBuffer hdr = ByteBuffer.allocate(zmqHdrSize);
    hdr.order(ByteOrder.BIG_ENDIAN);
    hdr.put(msgType);
    hdr.put(flags);
    hdr.putShort(seqNo);
    hdr.putInt(companyId);
    hdr.putInt(crawlerId);
    return hdr.array();
  }
}
