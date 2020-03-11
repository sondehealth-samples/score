//
//  MeasuresListViewController.swift
//  Sonde_iOS_Sample
//
//  Created by raj on 22/02/20.
//  Copyright © 2020 test. All rights reserved.
//

import UIKit
import AVFoundation

/**
 This class is created to show the activity instructions. 
 */



class ActivityInstructionViewController: UIViewController {
    let activityIndicator = UIActivityIndicatorView(style: .gray)
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //Step:1
        self.addActivityIndicator()
        APIHandler().getAuthToken(successCompletion: {_ in
            
            //Step:2
            
            APIHandler().getMeasures(successCompletion: {measures in
                self.removeActivityIndicator()
                APIHandler.measures = measures
            }, errorCompletion: {_ in
                self.removeActivityIndicator()
            })
            
            // Step: 3 (Step 2 and three do not have any dependency on each other)
            APIHandler().createUser(successCompletion: {_ in}, errorCompletion: {_ in})
            
            
        }, errorCompletion: {_ in
            self.removeActivityIndicator()
        })
        
    }
    
    func addActivityIndicator(){
        DispatchQueue.main.async {
            self.activityIndicator.hidesWhenStopped = true
            self.activityIndicator.startAnimating()
            self.activityIndicator.center = self.view.center
            self.view.addSubview(self.activityIndicator)
            self.view.isUserInteractionEnabled = false
        }
    }
    
    func removeActivityIndicator(){
        DispatchQueue.main.async {
            self.activityIndicator.removeFromSuperview()
            self.view.isUserInteractionEnabled = true
        }
    }
    
    @IBAction func startActivityClick(_ sender: UIButton) {
        self.performSegue(withIdentifier: "RecordingActivity", sender: self)
        
    }
}

