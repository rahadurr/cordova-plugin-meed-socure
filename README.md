# cordova-plugin-meed-socure

This is the Native **Socure** Document Scan **iOS**, & **Android** Cordova Plugin for [MeedBankingClub](https://meedbankingclub.com) App.

## Supported Platform:

- \[ ] iOS.
- \[X] Android.

## Changelog:

**Testing Release.**

## Installation:

```console
ionic cordova plugin add cordova-plugin-meed-socure

npm install @meed-native/socure
```

## Permission Required:

```xml

// Android
<uses-permission android:name="android.permission.CAMERA"></uses-permission>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"></uses-permission>
<uses-permission android:name="android.permission.INTERNET"></uses-permission>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>
```

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
    .then((result: LicenseScanResult) => {
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

## API

### Enums

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

### Interfaces:

```typescript
interface LicenseScanResult {
  licenseFrontImage: string;
  licenseBackImage: string;
}
```

```typescript
interface PassportScanResult {
  passportImage: string;
}
```

```typescript
interface SelfieScanResult {
  selfieImage: string;
}
```

### Actions:

```typescript

checkPermissions(permissions: string[]): Promise<PermissionStatus>;

scanLicense(): Promise<LicenseScanResult>;

scanPassport(): Promise<PassportScanResult>;

scanSelfie(): Promise<SelfieScanResult>;

openSetting(): Promise<boolean>;
```

## License

[The MIT License](./LICENSE.md)
