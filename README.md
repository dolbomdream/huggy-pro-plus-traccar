# [Traccar](https://www.traccar.org)

## Overview

[Traccar web app](https://github.com/traccar/traccar-web)를 기반으로 custom해서 만든 돌봄드림 GPS 위치 서비스.

traccar에서 참고 코드:

- [Traccar](https://github.com/traccar/traccar)
- [Traccar docker](https://github.com/traccar/traccar-docker)
- [Traccar web app](https://github.com/traccar/traccar-web)
- [Traccar Manager Android app](https://github.com/traccar/traccar-manager-android)
- [Traccar Manager iOS app](https://github.com/traccar/traccar-manager-ios)
- [Traccar Client Android app](https://github.com/traccar/traccar-client-android)
- [Traccar Client iOS app](https://github.com/traccar/traccar-client-ios)

## 커스텀 내용

1. [Traccar docker](https://github.com/traccar/traccar-docker)코드를 custom/docker안으로 옮기면서 코드 수정
1. Github Actions 내용 수정: .github/workflows/release.yml에서 도커 이미지를 acr로 push
1. traccar web 수정(로고, 아이콘)

## 사용법

1. **Create work directories:**

   ```bash
   mkdir -p /opt/traccar/logs
   ```

1. **Download mysql ssl cert file**

   ```bash
   wget https://dl.cacerts.digicert.com/DigiCertGlobalRootCA.crt.pem -P /opt/traccar
   ```

1. **get access Azure Container Registry(acr)**

   azure cli(az) 미 설치시 [az install](https://learn.microsoft.com/en-us/cli/azure/install-azure-cli)

   ```
   az login
   az acr login --name dbdream
   ```

1. **Get default traccar.xml:** 돌봄드림 Azure Container Registry(acr)접근 권한 필요

   ```bash
   docker pull dbdream.azurecr.io/dbdream/huggy-pro-plus-traccar:latest

   docker run \
   --rm \
   --entrypoint cat \
   dbdream.azurecr.io/dbdream/huggy-pro-plus-traccar:latest \
   /opt/traccar/conf/traccar.xml > /opt/traccar/traccar.xml
   ```

1. **Edit traccar.xml:** <https://www.traccar.org/configuration-file/>

   파일안에 mysql DB 접속정보 수정 (useSSL, sslrootcert는 traccar example 문서와 다름.)

   ```xml
      <entry key='database.driver'>com.mysql.cj.jdbc.Driver</entry>
      <entry key='database.url'>jdbc:mysql://{HOST}:{PORT}/{DB}?zeroDateTimeBehavior=round&amp;serverTimezone=UTC&amp;allowPublicKeyRetrieval=true&amp;useSSL=true&amp;sslrootcert=/opt/traccar/conf/DigiCertGlobalRootCA.crt.pem&amp;allowMultiQueries=true&amp;autoReconnect=true&amp;useUnicode=yes&amp;characterEncoding=UTF-8&amp;sessionVariables=sql_mode=''</entry>
      <entry key='database.user'>{USER}</entry>
      <entry key='database.password'>{PASSWORD}</entry>

      <entry key='geolocation.enable'>true</entry>
      <entry key='geolocation.type'>google</entry>
      <entry key='geolocation.key'>{GOOGLE API KEY}</entry>

   ```

1. **Create container:**
   ```bash
   docker run \
   --name traccar \
   --hostname traccar \
   --detach --restart unless-stopped \
   --publish 80:8082 \
   --publish 5000-5250:5000-5250 \
   --publish 5000-5250:5000-5250/udp \
   --volume /opt/traccar/logs:/opt/traccar/logs:rw \
   --volume /opt/traccar/traccar.xml:/opt/traccar/conf/traccar.xml:ro \
   --volume /opt/traccar/DigiCertGlobalRootCA.crt.pem:/opt/traccar/conf/DigiCertGlobalRootCA.crt.pem:ro \
   dbdream.azurecr.io/dbdream/huggy-pro-plus-traccar:latest
   ```

## Features

Some of the available features include:

- Real-time GPS tracking
- Driver behaviour monitoring
- Detailed and summary reports
- Geofencing functionality
- Alarms and notifications
- Account and device management
- Email and SMS support

## Build

Please read [build from source documentation](https://www.traccar.org/build/) on the official website.
