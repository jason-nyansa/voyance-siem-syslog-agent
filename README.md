# Voyance SIEM Syslog Agent [![Build Status](https://travis-ci.com/Nyansa/voyance-siem-syslog-agent.png?branch=master)](https://travis-ci.com/Nyansa/voyance-siem-syslog-agent)


Integration of Nyansa Voyance's [GraphQL API v2](https://nyansa.github.io/api-v2/) to SIEM through
Syslog. This implementation can be used as a reference and starting point to create your own custom
integration.

### Requirements
- Linux or macOS
- Java 1.8 or above
- at least 2GB of free memory for the JVM
##### For building a distributable package from source
- Apache Maven 3.3.9 or above

## Getting started
### Building package
Before building, checkout a tagged release version, e.g.

```bash
git checkout tags/v0.9.2 -b release-0.9.2
```

To build a package from source, in the project's base directory run:

```bash
mvn clean package
```

This should generate two packages under `target` with the name `VoyanceSiemSyslogAgent-${version}-package.{tar.gz,zip}`.
Pick your preferred file format and untar/unzip it to any directory you like, e.g.

```bash
tar xf VoyanceSiemSyslogAgent-0.9.2-package.tar.gz
```

Inside the unzipped directory you should see a script `VoyanceSiemSyslogAgent.sh`, directories
`config` and `jars`.

### Configuration
Before you can launch the agent there are two configuration files you will need to edit:

##### `config/config.properties`
This is the agent's configuration file, you will need to provide your Voyance GraphQL v2 URL and
access token. In addition, you can configure which API data fetches you want to enable.

```properties
# ======================================================================
# Voyance GraphQL API v2 URL
#
# description:  The Voyance GraphQL API v2 URL, refer to documentation:
#               https://support.nyansa.com/hc/en-us/articles/360020743351
#
# example:      https://yourcompany.nyansa.com/api/v2/graphql
# required:     yes
# ======================================================================
voyance.dev.api.url=

# ======================================================================
# Voyance GraphQL API v2 Access Token
#
# description:  The Voyance GraphQL API v2 access token for authentication.
#               https://support.nyansa.com/hc/en-us/articles/360020743351
#
# example:      ivG9B_66Bujc-RDaL4cA
# required:     yes
# ======================================================================
voyance.dev.api.token=

# ======================================================================
# API Data Fetches Enabled
#
# description:  A comma-separated list of API fetch IDs that should be
#               enabled.
#
#               The current available API fetch IDs are:
#               iotOutlierList_all
#               iotDeviceStatsList_last3h
#               iotDeviceStatsList_last24h
#               iotDeviceStatsList_last7d
#               iotDeviceStatsList_last14d
#               iotGroupStatsList_last3h
#               iotGroupStatsList_last24h
#               iotGroupStatsList_last7d
#               iotGroupStatsList_last14d
#               deviceList_updated
#
# example:      iotOutlierList_all,iotDeviceStatsList_last24h
# required:     yes
# ======================================================================
api.fetches.enabled=iotOutlierList_all,iotDeviceStatsList_last24h

...
```

##### `config/log4j2.xml`
This is the [Log4j](https://logging.apache.org/log4j/2.x/manual/configuration.html) configuration
file, you will need to edit these properties to point to your Syslog host/port using a protocol.

```xml
<Properties>
  <!--  SIEM Syslog Properties

        Change the following properties to your Syslog configuration.
        SiemSyslogHost:     The name or address of your Syslog host listening for log events.
        SiemSyslogPort:     The port of your Syslog host.
        SiemSyslogProtocol: "TCP" or "UDP"

        Refer to Log4j's documentation for SyslogAppender:
        https://logging.apache.org/log4j/2.x/manual/appenders.html#SyslogAppender
        Advanced users can further tune the <Syslog> appender below to fit your needs.
  -->
  <Property name="SiemSyslogHost">localhost</Property>
  <Property name="SiemSyslogPort">6514</Property>
  <Property name="SiemSyslogProtocol">TCP</Property>
  ...
</Properties>
```

There are other customizations you can make such as pull frequency and output format and they are
documented in either config files. You can also modify the `queries/*.graphql` files to tune the
GraphQL query you want to make against each endpoint.

The latest config file templates are available in this Github repo:
- [config.properties](https://github.com/Nyansa/voyance-siem-syslog-agent/blob/0.9/src/main/resources/config.properties)
- [log4j2.xml](https://github.com/Nyansa/voyance-siem-syslog-agent/blob/0.9/src/main/resources/log4j2.xml)

### Launching
Once the configurations are done, launch the agent with the provided `VoyanceSiemSyslogAgent.sh`
script:

```bash
./VoyanceSiemSyslogAgent.sh
```
```
Usage: ./VoyanceSiemSyslogAgent.sh { start | stop | restart | status | db_reset }
      start:      starting the the agent
      stop:       stopping the the agent
      restart:    restart the agent
      status:     check whether the agent is running
      db_reset:   resetting the agent's local database and all API fetch progress to its initial state
      validate:   validate "config.properties" for configuration errors
      show_apis:  show all available API fetch IDs
```

The usage should be self-explanatory, once the agent is started, monitor any errors in the log file,
e.g.

```
tail -f logs/voyance-agent.log
```

Common errors involve missing API URL and token, Syslog host/port not properly open or blocked by
firewall.

The agent automatically keeps track of each API fetch progress and ensures only new data will be
sent between pulls and restarts. It does so by maintaining a local database file
`voyance-agent.mv.db`. You can reset the progress by using the `db_reset` command in the launch
script.

## Customizations
The `config.properties` and `log4j2.xml` files contain certain level of configurations and
customizations such as API fetches to enable, API pull frequency, log format per API data type, and
more detailed Syslog settings etc.

But there are only so many customizations this reference implementation can provide.
It is possible to modify and extend the source code to suit your exact integration needs. The Java
classes `VoyanceSiemSyslogAgent`, `ApiPaginatedFetch`, and `ApiOutputAdapter` would be good places
to start.

##### Adding new API fetches
1. Have your GraphQL query and response data model ready. Add corresponding POJO classes under the
`com.nyansa.siem.api.models` package for JSON data binding. We use Jackson JSON for API data
marshalling.
2. Implement an "API fetch" class by extending `ApiPaginatedFetch`. There are a number of methods
that need to be overridden and you can look into how `IoTOutlierListFetch` is done for an example.
Each API fetch will need to have a corresponding `${apiEndpoint}.graphql` file containing the query.
The API data will be fetched page by page and marshalled into the POJO classes. API elements will
be emitted to the output adapter (currently Syslog).
3. Add the new API fetch handle to `VoyanceSiemSyslogAgent.AllAvailableApiFetches`, now users can
edit `config.properties` to enable and configure it.

##### Adding new output adapters
1. Other output adapters besides Syslog can also be added by extending `ApiOutputAdapter`, look at
`ApiSyslogAdapter` for an example.
2. Once a new adapter class is implemented inject an instance to your API fetch handle.

##### Dependencies
Minimum dependencies are used:
- [Apache HttpComponents](https://hc.apache.org/) for HTTP and GraphQL API data fetching
- [Jackson](https://github.com/FasterXML/jackson) for JSON marshalling.
- [H2](https://h2database.com/html/main.html) the embedded SQL database for states persistence.
- [Log4j](https://logging.apache.org/log4j/2.x/) for logging and Syslog support.

## License
Apache License Version 2.0, see [LICENSE](https://github.com/Nyansa/voyance-siem-syslog-agent/blob/master/LICENSE.txt)

For dependency licenses, see [DEPENDENCIES](https://github.com/Nyansa/voyance-siem-syslog-agent/blob/master/DEPENDENCIES.txt)
