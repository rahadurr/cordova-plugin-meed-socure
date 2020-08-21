# cordova-plugin-meed-socure

This is the Native **Socure** Document Scan **iOS**, & **Android** Cordova Plugin for [MeedBankingClub](https://meedbankingclub.com) App.

## Supported Platform:

- \[X] iOS.
- \[X] Android.

## Minimum Requirement
| Scan Mode  | Android          | iOS           |
| ------------- | ------------- | ------------- |
| Document and Selfie Capture  | minSdkVersion Version 22  | **iOS** 12 and above  |
| Barcode Data Extraction on Device  | minSdkVersion Version 22  | **iOS** 12 and above  |
| MRZ Data Extraction on Device  | minSdkVersion Version 22  | **iOS** 13 and above  |


## Changelog:

**Testing Release.**

## Installation:

```console
ionic cordova plugin add cordova-plugin-meed-socure

npm install @meed-native/socure
```
You can also use  `--variable SOCURE_SDK_KEY="sd654s6d54f-*-*"`
## Plugin Variable
`config.xml`
```xml
<plugin name="cordova-plugin-meed-socure" spec="latest">

  <variable name="SOCURE_SDK_KEY" value="sd654s6d54f-*-*" />

</plugin>
```
Or
`package.json`
```json
"cordova": {
    "plugins": {

      "cordova-plugin-meed-socure": {
        "SOCURE_SDK_KEY": "sd654s6d54f-*-*",
      }
      
    }
}
```
**Note**.

> If you add **SOCURE_SDK_KEY** on both config.xml and package.json file, package.json will override the config.xml *SOCURE_SDK_KEY* value.
## Permission Required:

```xml
<!--  iOS -->

<key>NSCameraUsageDescription</key>
<string>Meed requires use your camera in order to capture your sensitive documents for processing</string>

<key>Privacy - Camera Usage Description</key>
<string>Meed requires use your camera in order to capture your sensitive documents for processing</string>

<key>Privacy - Location Always Usage Description</key>
<string>Meed requires use your location in order to identify locales for processing documents</

<key>Privacy - Location When In Use Usage Description</key>
<string>Meed requires use your location in order to identify locales for processing documents</

<key>Privacy - Location Always and When In Use Usage Description</key>
<string>Meed requires use your location in order to identify locales for processing documents</string>


<!-- Android -->

<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## Plugin Customizations:
During First time Installation the `cordova-plugin-meed-socure` plugin a configuration directory automatically generate for Socure SDK customization with the name of **socure** in to the Application Root directory. This Directory contain Platform (iOS, Android) specific configuration files for **Socure SDK**.
```console
socure
├── android
│   ├── config.json
│   ├── strings.xml
│   └── styles.xml
└── ios
    ├── Document.json
    ├── Document.strings
    └── config.plist
```

### Android
| File Name     | Details       |
| ------------- | ------------- |
| strings.xml  | You can edit this file to change the texts for the capture screen components.  |
| styles.xml  | You can edit this file to change the display color and size of the capture components.  |
| **config.json**  | You can edit this file to change the capture properties as defined below.  |

### iOS
| File Name     | Details       |
| ------------- | ------------- |
| Document.strings  | You can edit this file to change the texts for the capture screen components.  |
| Document.json  | You can edit this file to change the display color and size of the capture components.  |
| **config.plist**  | You can edit this file to change the capture properties as defined below.  |

--- 
## Document Capture Customizations:

### Android

| Property Name  | Description  | Allowed Values| Default       |
| ------------- | ------------- | :-------------: | :-------------: |
| show_cropper  | Displays the capture frame for documents.  | Boolean (TRUE/FALSE)  | TRUE  |
| only_manual_capture  | To disable the auto-capture of documents.  | Boolean (TRUE/FALSE) | FALSE  |
| manual_timeout  | Timeout after which the option for manual capture pops.  | Number >1 | 10  |
| document_showconfirmation_screen  | Shows document preview from Socure to confirm the captured images.  | Boolean (TRUE/FALSE)  | TRUE  |
| enable_flash_capture  | Enable the flash image capture.  | Boolean (TRUE/FALSE)  | FALSE  |
| enable_help  | Shows a help button at the bottom for the additional help text.  | Boolean (TRUE/FALSE)  | TRUE  |
| selfie_manual_capture  | Disable the auto-capture.  | Boolean (TRUE/FALSE)  | FALSE  |
| selfie_manual_timeout  | Timeout after which the option for manual capture pops.  | Number >1  | 10  |
| selfie_showconfirmation_screen  | Shows selfie preview from Socure to confirm.  | Boolean (TRUE/FALSE)  | TRUE  |
| selfie_enable_help  | Shows a help button at the bottom for the additional help text.  | Boolean (TRUE/FALSE)  | TRUE  |

### iOS
| Property Name  | Description  | Allowed Values| Default       |
| ------------- | ------------- | :-------------: | :-------------: |
| show_cropper  | Displays the capture frame for documents.  | Boolean (YES/NO)  | YES  |
| only_manual_capture  | To disable the auto-capture of documents.  | Boolean (YES/NO) | NO  |
| manual_timeout  | Timeout after which the option for manual capture pops.  | Number >1 | 10  |
| document_showconfirmation_screen  | Shows document preview from Socure to confirm the captured images.  | Boolean (YES/NO)  | YES  |
| enable_flash_capture  | Enable the flash image capture.  | Boolean (YES/NO) | NO  |
| enable_help  | Shows a help button at the bottom for the additional help text.  | Boolean (YES/NO)  | YES  |
| selfie_manual_capture  | Disable the auto-capture.  | Boolean (YES/NO) | NO  |
| selfie_manual_timeout  | Timeout after which the option for manual capture pops.  | Number >1  | 10  |
| selfie_showconfirmation_screen  | Shows selfie preview from Socure to confirm. | Boolean (YES/NO)  | YES  |
| selfie_enable_help  | Shows a help button at the bottom for the additional help text.  | Boolean (YES/NO)  | YES  |
---

## Getting Start:

```typescript
// app.module.ts
import { Socure } from '@meed-native/socure/ngx';

@NgModule({
  ...

  providers: [
    ...
    Socure
    ...
  ]
  ...
})
export class AppModule { }
```


## 📷 Scan License

To Scan user License Document from user device. We can use
`scanLicense()`
method from plugins.

```typescript
import { Component } from '@angular/core';
import { Socure, LicenseScanResult } from '@meed-native/socure/ngx';

@Component({
  ...
})
export class HomePage {
  constructor(
    private socure: Socure
  ) {}

  scanLicense() {

    this.socure.scanLicense()
    .then((result: LicenseScanResult) => {
      console.log(result);
    })
    .catch((error: any) => {
      console.log(error)
    });

  }

}
```

## 📷 Scan Passport

To Scan user Passport Document from user device. We can use
`scanPassport()`
method from plugins.

```typescript
import { Component } from '@angular/core';
import { Socure, PassportScanResult } from '@meed-native/socure/ngx';

@Component({
  ...
})
export class HomePage {
  constructor(
    private socure: Socure
  ) {}

  scanPassport() {

    this.socure.scanPassport()
    .then((result: PassportScanResult) => {
      console.log(result);
    })
    .catch((error: any) => {
      console.log(error)
    });

  }

}
```

## 📷 Scan Selfie

To Scan user Selfie from user device. We can use
`scanSelfie()`
method from plugins.

```typescript
import { Component } from '@angular/core';
import { Socure, SelfieScanResult } from '@meed-native/socure/ngx';

@Component({
  ...
})
export class HomePage {
  constructor(
    private socure: Socure
  ) {}

  scanSelfie() {

    this.socure.scanSelfie()
    .then((result: SelfieScanResult) => {
      console.log(result);
    })
    .catch((error: any) => {
      console.log(error)
    });

  }

}
```

## 🔐 Check Permissions

To check user permissions from user device. We can use
`checkPermissions(permissions: Permissions)`
method from plugins.

```typescript
import { Component } from '@angular/core';
import { Socure, Permissions, PermissionStatus } from '@meed-native/socure/ngx';

@Component({
  ...
})
export class HomePage {
  constructor(
    private socure: Socure
  ) {}

  checkPermissions() {

    const permissions: Permissions[] = [Permissions.Camera, Permissions.Location]

    this.socure.checkPermissions(permissions)
    .then((status: PermissionStatus) => {
      console.log(status);
    })
    .catch((error: any) => {
      console.log(error)
    });
    
  }
}
```

## 🔧 Open Setting

For manually allow Camera & Location Permission for go to setting menu. We can use
`openSetting()`
method from plugins.

```typescript
import { Component } from '@angular/core';
import { Socure } from '@meed-native/socure/ngx';

@Component({
  ...
})
export class HomePage {
  constructor(
    private socure: Socure
  ) {}

  openSetting() {

    this.socure.openSetting()
    .then((result: boolean) => {
      console.log(result);
    })
    .catch((error: any) => {
      console.log(error)
    });

  }

}
```

# API

## Enums

```typescript
enum Permissions {
  Camera = "camera",
  Location = "location",
}
```

```typescript
enum PermissionStatus {
  Granted = 100,
  DeniedOnce = 200,
  DeniedAlways = 300,
}
```

## Interfaces:

```typescript
interface LicenseScanResult {
  licenseFrontImage: string;
  licenseBackImage: string;
  barcodeData: BarcodeData;
}
```

```typescript
interface BarcodeData {
  firstName: string;
  lastName?: string;       // iOS only
  middleName?: string;     // iOS only
  fullName: string;
  dob: string;
  address: string;
  city: string;
  state: string;
  postalCode: string;
  documentNumber: string;
  issueDate: string;
  expirationDate: string;
}
```

```typescript
interface PassportScanResult {
  passportImage: string;
  mrzData: MRZData;
}
```

```typescript
interface MRZData {
  code?: string;            // iOS only
  format?: string;          // iOS only
  surName: string;
  firstName: string;
  issuingCountry: string;
  nationality: string;
  sex?: string;             // iOS only
  dob: string;
  documentNumber: string;
  expirationDate: string;
  validDocumentNumber?: boolean;    // iOS only
  validDateOfBirth?: boolean;       // iOS only
  validExpirationDate?: boolean;    // iOS only
  validComposite?: boolean;         // iOS only
}
```

```typescript
interface SelfieScanResult {
  selfieImage: string;
}
```

## Actions:

```typescript

checkPermissions(permissions: Permissions[]): Promise<PermissionStatus>;

scanLicense(): Promise<LicenseScanResult>;

scanPassport(): Promise<PassportScanResult>;

scanSelfie(): Promise<SelfieScanResult>;

openSetting(): Promise<boolean>;
```

## License

[The MIT License](./LICENSE.md)
