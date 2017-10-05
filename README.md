# Filkom News Reader Android
[![Build Status](https://travis-ci.org/ngengs/filkom-news-reader_application-android.svg?branch=development)](https://travis-ci.org/ngengs/filkom-news-reader_application-android)
[![GitHub release](https://img.shields.io/github/release/ngengs/filkom-news-reader_application-android.svg)](https://github.com/ngengs/filkom-news-reader_application-android/releases/latest)

![Logo](/.github/logo/logo.png?raw=true)

This is Android application to consume API from the [Filkom News Reader Server](https://github.com/ngengs/filkom-news-reader_server) and give you latest news and announcement from [Filkom](http://filkom.ub.ac.id).


## Feature
* Show you latest **news** and **announcement** from Filkom
* Show detail **news**
* Open announcement in your browser
* Share **news** and **announcement** link with title to other apps
* Push notification for **news** and **announcement**
* Configuration for active push notification

## Preview
* Phone

![Screenshot](/.github/screenshot/phone-1.png?raw=true)
![Screenshot](/.github/screenshot/phone-2.png?raw=true)
![Screenshot](/.github/screenshot/phone-3.png?raw=true)
![Screenshot](/.github/screenshot/phone-4.png?raw=true)
![Screenshot](/.github/screenshot/phone-5.png?raw=true)

* Tablet

![Screenshot](/.github/screenshot/tablet-1.png?raw=true)
![Screenshot](/.github/screenshot/tablet-2.png?raw=true)

### Build
#### Requirement
- Java 8
- Android SDK

#### Preparation
- Clone this [Repo](https://github.com/ngengs/filkom-news-reader_application-android)
- Prepare your sign key for release and debug build, read [here](https://developer.android.com/studio/publish/app-signing.html) if you dont know how to do that
- Rename `example.keystore.properties` to `keystore.properties`
- Change value inside `keystore.properties`

#### Generate Releases APK
- Run command
  ```
  $ ./gradlew clean assembleRelease
  ```
- Your apk will located at `app/build/outputs/apk/release/`

## Author
**Rizky Kharisma** (https://github.com/ngengs)

### License

    Copyright 2017 Rizky Kharisma (@ngengs).

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.