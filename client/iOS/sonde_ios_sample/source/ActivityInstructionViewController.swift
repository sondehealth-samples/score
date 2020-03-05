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
    override func viewDidLoad() {
        super.viewDidLoad()
    }
   
    @IBAction func startActivityClick(_ sender: UIButton) {
        self.performSegue(withIdentifier: "RecordingActivity", sender: self)
       
    }
}
