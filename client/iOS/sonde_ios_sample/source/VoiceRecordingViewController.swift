//
//  ViewController.swift
//  Sonde_iOS_Sample
//
//  Created by raj on 22/02/20.
//  Copyright © 2020 test. All rights reserved.
//

import UIKit
import AVFoundation

/**
 This class has the logic to voice recording and get the score.
*/

class VoiceRecordingViewController: UIViewController {
    let recordingSession = AVAudioSession.sharedInstance()
    var audioRecorder: AVAudioRecorder!
    var isStartted = false
    var audioFilePath:URL?
    
    let activityIndicator = UIActivityIndicatorView(style: .gray)
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.title = "Voice Recording"
        audioFilePath = getDocumentsDirectory().appendingPathComponent(NSUUID().uuidString + ".wav")
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(true)
        
        askForAudioRecordingPermission()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        audioRecorder.stop()
    }

    @IBAction func btnStartRecordingClick(_ sender: UIButton) {
        if !isStartted{
            self.startRecording()
            sender.setTitle("Stop Recording", for: .normal)
        }else{
             self.stopRecording()
            sender.setTitle("Start Recording", for: .normal)
        }
        
        isStartted = !isStartted
        
    }
    
    func stopRecording(){
        audioRecorder.stop()
        self.addActivityIndicator()
        
        //##########Get the signed URL.
        // You can get user identifier from your server when user onboarded.
        let measureName = (UIApplication.shared.delegate as! AppDelegate).measures.first ?? "resilience"
        APIHandler().getSignedURL(countryCode: "IN", fileType: "wav", userIdentifier: "427df6a3f", completion: {response in
            
            //###########Upload the file
            let signedURL = response["signedURL"] as! String
            let filePath = response["filePath"] as! String
            print(filePath)
            print("Got the signed URL")
            APIHandler().uploadFile(signedURL: signedURL, audioFileURL: self.audioFilePath!, completion: {
                
                print("File got uploaded")
                
                //##########Get the Score
                APIHandler().getScore(filePath: filePath, measureName: measureName, userIdentifier:"427df6a3f",  completion: {scoreResponse in
                    print("Score is fetched")
                    print(scoreResponse)
                    DispatchQueue.main.async {
                        self.removeActivityIndicator()
                        if let score = scoreResponse["score"] as? Double{
                            var message = "Measure Name: " + (scoreResponse["name"] as! String) + "\n"
                            message = message + "Score: " + Int(score).description
                            let alertController = UIAlertController(title: "Measure Score", message: message, preferredStyle: .alert)
                            let action = UIAlertAction(title: "OK", style: .default, handler: {action in
                                self.navigationController?.popViewController(animated: true)
                            })
                            alertController.addAction(action)
                            
                            self.present(alertController, animated: true, completion: nil)
                        }
                    }
                }, errorCompletion: {error in
                    DispatchQueue.main.async {
                        self.removeActivityIndicator()
                    }
                })
            }, errorCompletion: {error in
                DispatchQueue.main.async {
                    self.removeActivityIndicator()
                }
            })
        }, errorCompletion: {error in
            DispatchQueue.main.async {
                self.removeActivityIndicator()
            }
        })
    }
    
    /**
     Ask the recording permission from user.
    */
    
    func askForAudioRecordingPermission(){
        do{
            try recordingSession.setCategory(.record)
            recordingSession.requestRecordPermission({allowed in
                if !allowed{
                    self.showPermissionError()
                }
            })
        }catch{}
    }
    
    /**
     Get the document directory path
    */
    func getDocumentsDirectory() -> URL {
        let paths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        return paths[0]
    }
    
    func showPermissionError(){
        let alerController = UIAlertController(title: "Permission acess error", message: nil, preferredStyle: .alert)
        alerController.addAction(UIAlertAction(title: "OK", style: .default, handler: nil))
        self.present(alerController, animated: true, completion: nil)
    }
    
    /**
     To start the recording it is recomended to use the given settings object.
    */
    
    func startRecording() {
        let settings: [String:Any] = [
            AVFormatIDKey: kAudioFormatLinearPCM,
            AVLinearPCMBitDepthKey:16,
            AVSampleRateKey: 44100.0,
            AVNumberOfChannelsKey: 1 as NSNumber,
            AVEncoderAudioQualityKey: AVAudioQuality.high.rawValue,
            AVAudioFileTypeKey: kAudioFileWAVEType
        ]
       
        do{
            audioRecorder = try AVAudioRecorder(url: audioFilePath!, settings: settings)
            audioRecorder.prepareToRecord()
            audioRecorder.record()
            
        }catch{}
    }
    
    func addActivityIndicator(){
        activityIndicator.hidesWhenStopped = true
        activityIndicator.startAnimating()
        activityIndicator.center = self.view.center
        
        self.view.addSubview(activityIndicator)
    }
    
    func removeActivityIndicator(){
        self.activityIndicator.removeFromSuperview()
    }
}

