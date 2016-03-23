//
//  ViewController.h
//  test3
//
//  Created by Jin Lee on 2016. 2. 25..
//  Copyright © 2016년 Jin Lee. All rights reserved.
//

#import <UIKit/UIKit.h>

#import <opencv2/opencv.hpp>
#import <opencv2/videoio/cap_ios.h>

#import "CoreMotionCatcher.h"
#import "VideoCamera.h"

using namespace cv;

@interface ViewController : UIViewController<CvVideoCameraDelegate>
{
    IBOutlet UIImageView* imageView;
    IBOutlet UIButton* closeBt;
    IBOutlet UIButton* shotBt;
    IBOutlet UILabel* label;
    VideoCamera* videoCamera;


    
}

@property (nonatomic) IBOutlet UILabel *label;

@property (nonatomic) IBOutlet UIView *myview;
@property (nonatomic) IBOutlet UIView *sensorRecogView;
@property (nonatomic) IBOutlet UIView *markerRecogView;


@property (nonatomic) VideoCamera* videoCamera;

- (IBAction)pushCloseBt:(id)sender;

- (IBAction)pushShotBt:(id)sender;


@end


