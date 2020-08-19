/********* Socure.swift Cordova Plugin Implementation *******/

import AVFoundation
import CoreLocation
import SocureSdk


/*
* Notes: The @objc shows that this class & function should be exposed to Cordova.
*/

@objc(Socure) class Socure : CDVPlugin, SocureScanResult {
    
  var locationManager: CLLocationManager?
    
  var invokedUrlcommand: CDVInvokedUrlCommand?
    
    @objc override func  pluginInitialize() {
        locationManager = CLLocationManager()
    }
    

  @objc(checkPermissions:) // Declare your function name.
  func checkPermissions(command: CDVInvokedUrlCommand) {
    
    let permissions: [String] = command.arguments[0] as! [String];
    
    var PERMISSION_STATUS = 0;
    
    for permission in permissions {
        switch permission {
        case "camera":
            // Camera Permission
            PERMISSION_STATUS =  self.checkCameraPermissionStatus()
            break;
        case "location":
            PERMISSION_STATUS =  self.checkLocationPermissionStatus()
            break;
            
        default: break
        }
    }
    
    let   pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: PERMISSION_STATUS);
    self.commandDelegate!.send(pluginResult, callbackId: command.callbackId);
  }
    
    
  @objc(scanLicense:)
  func scanLicense(command: CDVInvokedUrlCommand) {
    self.invokedUrlcommand = command
    let controller =  SocureViewController(socureScaneMode: SocureScaneMode.License);
    controller.modalPresentationStyle = .fullScreen
    controller.delegate = self
    
    self.viewController.present(controller, animated: false, completion: nil)
  }
    

  @objc(scanPassport:)
  func scanPassport(command: CDVInvokedUrlCommand) {
    self.invokedUrlcommand = command
    let controller =  SocureViewController(socureScaneMode: SocureScaneMode.Passport);
    controller.modalPresentationStyle = .fullScreen
    controller.delegate = self
     
    self.viewController.present(controller, animated: false, completion: nil)
  }
    
    
  @objc(scanSelfie:)
  func scanSelfie(command: CDVInvokedUrlCommand) {
    self.invokedUrlcommand = command
      
    let controller =  SocureViewController(socureScaneMode: SocureScaneMode.Selfie);
    controller.modalPresentationStyle = .fullScreen
    controller.delegate = self
      
    self.viewController.present(controller, animated: false, completion: nil)
  }
    

  @objc(openSetting:)
  func openSetting(command: CDVInvokedUrlCommand) {
    if let url = NSURL(string: UIApplicationOpenSettingsURLString) as URL? {
      UIApplication.shared.open(url)
      let pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: true);
      self.commandDelegate!.send(pluginResult, callbackId: command.callbackId);
    }
  }
    
    
    
  /********* Permissions Check Private Method *******/
    
    func checkCameraPermissionStatus() -> Int {
        var status = 0;
        let cameraPermissionStatus = AVCaptureDevice.authorizationStatus(for: AVMediaType.video)
        
        if cameraPermissionStatus == .authorized {
            status = 100
        } else if (cameraPermissionStatus == .restricted) {
             status = 200
        } else if (cameraPermissionStatus == .denied) {
            status = 300
        } else if (cameraPermissionStatus == .notDetermined) {
            AVCaptureDevice.requestAccess(for: .video) { success in
                if success {
                    status = 100
                } else {
                    status = 300
                }
            }
        }
        
        return status
    }
    
    func checkLocationPermissionStatus() -> Int {
        var status = 0;
        let locationPermissionStatus = CLLocationManager.authorizationStatus()
        
        switch locationPermissionStatus {
        case CLAuthorizationStatus.authorizedAlways:
            status = 100
            break;
        case CLAuthorizationStatus.authorizedWhenInUse:
            status = 100
            break;
        case CLAuthorizationStatus.denied:
            status = 300
            break;
        case CLAuthorizationStatus.restricted:
            status = 200
            break;
        case CLAuthorizationStatus.notDetermined:
            locationManager?.requestWhenInUseAuthorization()
            break;
        default: break
            
        }
        
        return status
    }
    

    
    
  /********* SocureScanResult  Delegate Protocol  Methods Implementation *******/
    
  @objc public func licanseScanResult(licenseScanResult: Dictionary<String, Any>) {
    let pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: licenseScanResult);
    self.commandDelegate!.send(pluginResult, callbackId: self.invokedUrlcommand?.callbackId);
      
  }
  
  @objc public func passportScanResult(passportScanResult: Dictionary<String, Any>) {
    let pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: passportScanResult);
    self.commandDelegate!.send(pluginResult, callbackId: self.invokedUrlcommand?.callbackId);
  }
  
  @objc public func selfieScanResult(selfieScanResult: Dictionary<String, String>) {
    let pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: selfieScanResult);
    self.commandDelegate!.send(pluginResult, callbackId: self.invokedUrlcommand?.callbackId);
  }
    
}

public enum SocureScaneMode {
  case License
  case Passport
  case Selfie
}

@objc public protocol SocureScanResult {
  func licanseScanResult(licenseScanResult: Dictionary<String, Any>)
  func passportScanResult(passportScanResult: Dictionary<String, Any>)
  func selfieScanResult(selfieScanResult: Dictionary<String, String>)
}
