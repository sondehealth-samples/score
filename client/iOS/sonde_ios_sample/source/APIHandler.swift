//
//  APIHandler.swift
//  Sonde_iOS_Sample
//
//  Created by raj on 24/02/20.
//  Copyright © 2020 test. All rights reserved.
//

import UIKit


class APIHandler: NSObject, URLSessionDelegate {
    static var accessToken = ""

    var baseURL = "https://d1d65rrfia4age.cloudfront.net/platform/v1/"
 
    /**
     Call to oauth2/token API to get the access token. Store the token in secure place. This token is required to call the sonde services.
     - Parameter successCompletion : Returns the success call back.
     - Parameter  token : The value of access token
     - Parameter errorCompletion : to return a error call back.
     - Parameter  error : The error message return in errorCompletion callback
     
     - Change url as per your configurations.
     
     
     */
    func getAuthToken(successCompletion:@escaping (_ token:String)->Void, errorCompletion:@escaping (_ error:String)->Void){
        guard let url = URL(string: "http://localhost:8080/oauth2/token") else {
            return
        }
        
        let session = URLSession(configuration: .default)
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        
        session.dataTask(with: request,completionHandler: {(data, response, error) in
            if error != nil{
                errorCompletion(error!.localizedDescription)
            }else{
                if let data = data, let tokenResponse = try? JSONSerialization.jsonObject(with: data, options: []) as? [String: AnyObject], let accessToken = tokenResponse["access_token"] as? String{
                    APIHandler.accessToken = accessToken
                    print(accessToken)
                    
                    successCompletion(accessToken)
                }else{
                    errorCompletion("Invalid Response")
                }
            }
        }).resume()
    }
    
    
    /**
     Call to measures API to get the list of measures assigned to you. You can store measures data into your database. You can get the socres for your measures.
     - Parameter successCompletion : Returns the success call back.
     - Parameter  measures : The list of measures assigned to you
     - Parameter errorCompletion : to return a error call back.
     - Parameter  error : The error message return in errorCompletion callback
     
     - Change url as per your configurations.
     
     
     */
    
    func getMeasures(successCompletion:@escaping (_ measures:[String])->Void, errorCompletion:@escaping (_ error:String)->Void){
        guard let url = URL(string:  baseURL + "measures") else {
            return
        }
        
        if APIHandler.accessToken.isEmpty{
            return
        }
        
        
        let session = URLSession(configuration: .default)
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue(APIHandler.accessToken, forHTTPHeaderField: "Authorization")
        
        session.dataTask(with: request, completionHandler: {(data, response, error) in
            if error != nil{
                errorCompletion(error!.localizedDescription)
            }else{
                if let data = data, let measuresResponse = try? JSONSerialization.jsonObject(with: data, options: []) as? [String: AnyObject], let measures = measuresResponse["measures"] as? [[String: Any]]{
                    if let measureNames = measures.map({$0["name"]}) as? [String]{
                        successCompletion(measureNames)
                    }else{
                        errorCompletion("Invalid Response")
                    }
                }else{
                    errorCompletion("Invalid Response")
                }
            }
        }).resume()
    }
    
    /**
     This method show how you can upload the recorded audio file.
     - Parameter signedURL : This is the URL where your audio file to be uploaded in sonde system. To get this value see the getSignedURL method
     - Parameter  audioFileURL : This is the your local audio file URL. Beofre going to upload the file, you need to save the file somewhere in your app's sandbox.
     - Parameter completion : Returns the success call back.
     - Parameter errorCompletion : to return a error call back.
     - Parameter  error : The error message return in errorCompletion callback
     
     - Change url as per your configurations.
     
     
     */
    
    func uploadFile(signedURL:String, audioFileURL:URL, completion:@escaping ()->Void, errorCompletion:@escaping (_ error:Error)->Void){
        guard let  uploadURL = URL(string: signedURL) else{
            return
        }
        if APIHandler.accessToken.isEmpty{
            return
        }
        
        let session = URLSession(configuration: .default)
        var request = URLRequest(url: uploadURL)
        request.httpMethod = "PUT"
        
        session.uploadTask(with: request, fromFile: audioFileURL, completionHandler: {(data, response, error) in
            if (response as? HTTPURLResponse)?.statusCode == 200{
                completion()
            }else{
                errorCompletion(error!)
            }
        }).resume()
    }
    
    /**
     This method show how you can upload the recorded audio file.
     - Parameter countryCode : This is your device country code which is setup from settings of your mobile.
     - Parameter  fileType : This the value of filetype supported by sonde system. Send the valid file type.
     - Parameter subjectIdentifier : This is the subject/user identifier which is returns from sonde while creating your users in sonde system.
     - Parameter completion : Returns the success call back
     - Parameter  storageResponse : It contains the signed URL and file location.
     - Parameter errorCompletion : to return a error call back.
     - Parameter  error : The error message return in errorCompletion callback
     
     - Change url as per your configurations.
     
     
     */
    
    func getSignedURL(countryCode: String, fileType: String, userIdentifier: String, completion:@escaping (_ storageResponse:[String:AnyObject])->Void, errorCompletion:@escaping (_ error:String)->Void){
        guard let url = URL(string: baseURL + "storage/files") else {
            return
        }
        if APIHandler.accessToken.isEmpty{
            return
        }
        
        let body:[String: String] = ["countryCode": countryCode, "fileType": fileType, "userIdentifier": userIdentifier]
        guard let bodyData = try? JSONSerialization.data(withJSONObject: body, options: []) else{
            return
        }
        
        
        let session = URLSession.shared
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue(APIHandler.accessToken, forHTTPHeaderField: "Authorization")
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.httpBody = bodyData

        session.dataTask(with: request,completionHandler: {(data, response, error) in
            if error != nil{
                errorCompletion(error!.localizedDescription)
            }else{
                if let data = data, let storageResponse = try? JSONSerialization.jsonObject(with: data, options: []) as? [String: AnyObject]{
                   completion(storageResponse)
                }else{
                    errorCompletion("Invalid Response")
                }
            }
        }).resume()
    
        
    }
    
    /**
     This method show how you can upload the recorded audio file.
     - Parameter fileLocation : Send the file location you received from getSignedURL method
     - Parameter  measureName : Send one of measure for which you want to get the score. To get the assigned measures refer getMeasures method description
     - Parameter completion : Returns the success call back
     - Parameter scoreResponse : Returns the score object with measureName.
     - Parameter errorCompletion : to return a error call back.
     - Parameter  error : The error message return in errorCompletion callback
     
     - Change url as per your configurations.
     
     
     */
    
    
    func getScore(filePath:String, measureName:String, userIdentifier:String, completion:@escaping (_ scoreResponse: [String:AnyObject])->Void, errorCompletion:@escaping (_ error:String)->Void){
        guard let url = URL(string: baseURL + "inference/scores") else {
            return
        }
        
        let body: [String: AnyObject] = ["filePath": filePath as AnyObject, "measureName" : measureName as AnyObject, "userIdentifier" : userIdentifier as AnyObject]
        guard let bodyData = try? JSONSerialization.data(withJSONObject: body, options: []) else{
            return
        }
        if APIHandler.accessToken.isEmpty{
            return
        }
        
        let session = URLSession(configuration: .default)
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.httpBody = bodyData
        request.setValue(APIHandler.accessToken, forHTTPHeaderField: "Authorization")
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        session.dataTask(with: request,completionHandler: {(data, response, error) in
            if error != nil{
               errorCompletion(error!.localizedDescription)
            }else{
                if let data = data, let responseJSON = try? JSONSerialization.jsonObject(with: data, options: []) as? [String: AnyObject]{
                    completion(responseJSON)
                }else{
                    
                    errorCompletion("Invalid Response")
                }
            }
        }).resume()
        
    }
}
