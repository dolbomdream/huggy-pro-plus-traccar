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

1. **Get default traccar.xml:** 돌봄드림 Azure Container Registry(acr)접근 권한 필요

   ```bash
   docker run \
   --rm \
   --entrypoint cat \
   dbdream.azurecr.io/dbdream/huggy-pro-plus-traccar:latest \
   /opt/traccar/conf/traccar.xml > /opt/traccar/traccar.xml
   ```

1. **Edit traccar.xml:** <https://www.traccar.org/configuration-file/>

   파일안에 mysql DB 접속정보 수정

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
