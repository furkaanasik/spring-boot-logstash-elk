# ELK Stack Log Visualization with Spring Boot

**Languages**: [EN](#english) | [TR](#turkce)

---

<a name="english"></a>
# ELK Stack Log Visualization with Spring Boot (English)

This project demonstrates how to set up an ELK (Elasticsearch, Logstash, Kibana) stack with a Spring Boot application for visualizing application logs.

## Project Overview

We use **Logback** for logging in the Spring Boot project, **Logstash** for ingesting the logs, **Elasticsearch** for storing them, and **Kibana** for visualizing the logs in real-time.

The ELK stack setup is based on [deviantony's docker-elk](https://github.com/deviantony/docker-elk) repository, with some modifications to meet our requirements, such as:

- Dynamic index creation based on the application name and the current date.
- Adjusted permissions for Logstash in Elasticsearch.

## Steps

1. Modify the `logstash.conf` file located at `logstash/pipeline/logstash.conf` to dynamically create indices using the application name and date:

   ```bash
   input {
       tcp {
           port => 50000
           codec => json_lines
       }
   }

   filter {
       mutate {
           add_field => { "application_name" => "%{[application-name]}" }
       }
   }

   output {
       elasticsearch {
           hosts => "elasticsearch:9200"
           user => "logstash_internal"
           password => "${LOGSTASH_INTERNAL_PASSWORD}"
           index => "%{[application_name]}-%{+YYYY.MM.dd}"
       }
   }
   ```

2. Update the permissions in `setup/roles/logstash_writer.json` to allow Logstash to create indices:

   ```json
   {
     "cluster": [
       "manage_index_templates",
       "monitor",
       "manage_ilm"
     ],
     "indices": [
       {
         "names": [
           "logs-generic-default",
           "logstash-*",
           "ecs-logstash-*",
           "*"
         ],
         "privileges": [
           "write",
           "create",
           "create_index",
           "manage",
           "manage_ilm",
           "read",
           "delete",
           "view_index_metadata",
           "auto_configure"
         ]
       }
     ]
   }
   ```

3. After setting up passwords in the `.env` file, run the following commands:

   ```bash
   docker compose up setup
   docker compose up -d
   ```

4. Add the following dependency to the Spring Boot project to log data to Logstash:

   ```xml
   <dependency>
       <groupId>net.logstash.logback</groupId>
       <artifactId>logstash-logback-encoder</artifactId>
       <version>8.0</version>
   </dependency>
   ```

5. Configure `logback-spring.xml` in the `resources` folder to send logs to Logstash:

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <configuration debug="true">
       <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
       <property name="spring.application.name" value="elk" scope="context"/>

       <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
           <encoder>
               <pattern>${CONSOLE_LOG_PATTERN}</pattern>
           </encoder>
       </appender>

       <appender name="ELK" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
           <destination>localhost:50000</destination>
           <encoder class="net.logstash.logback.encoder.LogstashEncoder">
               <customFields>
                   {
                   "application-name":"${spring.application.name}"
                   }
               </customFields>
               <provider class="net.logstash.logback.composite.loggingevent.ArgumentsJsonProvider"/>
           </encoder>
       </appender>

       <root level="INFO">
           <appender-ref ref="CONSOLE"/>
           <appender-ref ref="ELK"/>
       </root>
   </configuration>
   ```

6. To view the logs in Kibana:

    - Access Kibana by navigating to `http://localhost:5601`.
    - Log in with the username `elastic` and the password set in the `.env` file.
    - From the left-hand menu, select **Management** > **Stack Management**.
    - In the **Data** section, click **Index Management** to view the created index.
    - Next, go to **Analytics** > **Discover**. If no logs appear, create a new data view:
        - Click **Create a new data view**.
        - Choose the appropriate index pattern, such as `elk-*`.
        - Save the data view, and the logs should now be visible.

---

<a name="turkce"></a>
# ELK Yığınıyla Logları Görselleştirme (Türkçe)

Bu proje, bir Spring Boot uygulamasında ELK (Elasticsearch, Logstash, Kibana) yığını kullanarak uygulama loglarını görselleştirmeyi gösterir.

## Proje Genel Bakışı

Spring Boot projesinde loglama için **Logback**, logları almak için **Logstash**, logları depolamak için **Elasticsearch**, ve logları gerçek zamanlı olarak görselleştirmek için **Kibana** kullanıyoruz.

ELK yığını, [deviantony'nin docker-elk](https://github.com/deviantony/docker-elk) deposuna dayanıyor, ancak birkaç değişiklik yaptık:

- Uygulama adı ve tarihe dayalı dinamik indeks oluşturma.
- Logstash için Elasticsearch'te izinler düzenlendi.

## Adımlar

1. Uygulama adı ve tarih ile indeks oluşturmak için `logstash/pipeline/logstash.conf` dosyasını şu şekilde güncelleyin:

   ```bash
   input {
       tcp {
           port => 50000
           codec => json_lines
       }
   }

   filter {
       mutate {
           add_field => { "application_name" => "%{[application-name]}" }
       }
   }

   output {
       elasticsearch {
           hosts => "elasticsearch:9200"
           user => "logstash_internal"
           password => "${LOGSTASH_INTERNAL_PASSWORD}"
           index => "%{[application_name]}-%{+YYYY.MM.dd}"
       }
   }
   ```

2. `setup/roles/logstash_writer.json` dosyasındaki izinleri Logstash'in indeks oluşturabilmesi için şu şekilde güncelleyin:

   ```json
   {
     "cluster": [
       "manage_index_templates",
       "monitor",
       "manage_ilm"
     ],
     "indices": [
       {
         "names": [
           "logs-generic-default",
           "logstash-*",
           "ecs-logstash-*",
           "*"
         ],
         "privileges": [
           "write",
           "create",
           "create_index",
           "manage",
           "manage_ilm",
           "read",
           "delete",
           "view_index_metadata",
           "auto_configure"
         ]
       }
     ]
   }
   ```

3. .env dosyasındaki şifreleri belirledikten sonra şu komutları çalıştırın:

   ```bash
   docker compose up setup
   docker compose up -d
   ```

4. Logstash'e veri göndermek için projeye şu bağımlılığı ekleyin:

   ```xml
   <dependency>
       <groupId>net.logstash.logback</groupId>
       <artifactId>logstash-logback-encoder</artifactId>
       <version>8.0</version>
   </dependency>
   ```

5. `resources` klasörü altında `logback-spring.xml` dosyasını şu şekilde oluşturun:

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <configuration debug="true">
       <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
       <property name="spring.application.name" value="elk" scope="context"/>

       <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
           <encoder>
               <pattern>${CONSOLE_LOG_PATTERN}</pattern>
           </encoder>
       </appender>

       <appender name="ELK" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
           <destination>localhost:50000</destination>
           <encoder class="net.logstash.logback.encoder.LogstashEncoder">
               <customFields>
                   {
                   "application-name":"${spring.application.name}"
                   }
               </customFields>
               <provider class="net.logstash.logback.composite.loggingevent.ArgumentsJsonProvider"/>
           </encoder>
       </appender>

       <root level="INFO">
           <appender-ref ref="CONSOLE"/>
           <appender-ref ref="ELK"/>
       </root>
   </configuration>
   ```

6. Kibana üzerinde logları görüntülemek için:

    - `http://localhost:5601` adresine giderek Kibana'ya erişin.
    - `elastic` kullanıcısı ve `.env` dosyasındaki şifre ile giriş yapın.
    - Sol menüde **Management** > **Stack Management** sekmesine gidin.
    - **Data** kısmında **Index Management** seçeneğine tıklayarak oluşturulan indeksleri görün.
    - Sonrasında **Analytics** > **Discover** kısmına gidin. Eğer loglar görünmüyorsa, yeni bir data view oluşturun:
        - **Create a new data view** seçeneğine tıklayın.
        - Uygun indeks desenini seçin, örneğin `elk-*`.
        - Data view'i kaydedin ve loglar artık görünür olmalıdır.