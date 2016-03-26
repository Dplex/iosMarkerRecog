//
//  ViewController.m
//  test3
//
//  Created by Jin Lee on 2016. 2. 25..
//  Copyright © 2016년 Jin Lee. All rights reserved.
//

#import "ViewController.h"
#import "ViewController2.h"
#include "MarkerRcgn.h"

Mat myMat;
CvPoint DisPoint[4];

static void UIImageToMat(const UIImage* image, cv::Mat &m){
    CGColorSpaceRef colorSpace = CGImageGetColorSpace(image.CGImage);
    CGFloat cols = image.size.width;
    CGFloat rows = image.size.height;
    
    m.create(rows, cols, CV_8UC4);
    CGContextRef contextRef = CGBitmapContextCreate(m.data, m.cols, m.rows, 8, m.step[0], colorSpace, kCGImageAlphaNoneSkipLast | kCGBitmapByteOrderDefault);
    CGContextDrawImage(contextRef, CGRectMake(0, 0, cols, rows), image.CGImage);
    CGContextRelease(contextRef);
    CGColorSpaceRelease(colorSpace);
}

@interface ViewController ()

@end

@implementation ViewController;

@synthesize videoCamera, myview, label, sensorRecogView, markerRecogView;

CoreMotionCatcher* catcher;

cv::Mat markerMat;

cv::Size imgViewSize;

- (void)viewDidLoad {
    [super viewDidLoad];

    NSString *markerPath = [NSString stringWithFormat:@"%@/marker.png", [[NSBundle mainBundle] resourcePath]];
    UIImage *resImage = [UIImage imageWithContentsOfFile:markerPath];
    UIImageToMat(resImage, markerMat);
    
    self.videoCamera = [[VideoCamera alloc] initWithParentView:tempView];
    self.videoCamera.defaultAVCaptureDevicePosition = AVCaptureDevicePositionBack;
    self.videoCamera.defaultAVCaptureSessionPreset = AVCaptureSessionPresetPhoto;
    self.videoCamera.defaultFPS = 30;
    self.videoCamera.grayscaleMode = NO;
    self.videoCamera.delegate = self;
    self.videoCamera.defaultAVCaptureVideoOrientation = AVCaptureVideoOrientationPortrait;
    
    self.sensorRecogView.layer.cornerRadius = 7;
    self.sensorRecogView.layer.masksToBounds = true;
    
    self.markerRecogView.layer.cornerRadius = 7;
    self.markerRecogView.layer.masksToBounds = true;
    self.myview.layer.cornerRadius = 25;
    self.myview.layer.masksToBounds = true;
    
    
    catcher = [[CoreMotionCatcher alloc] init];
    [catcher Start:self.myview sensorView:self.sensorRecogView];
    [self.videoCamera start];
    imgViewSize.height = imageView.frame.size.height;
    imgViewSize.width = imageView.frame.size.width;
}


+(UIImage* )imageWithCVMat
{
    
    NSData *data = [NSData dataWithBytes:myMat.data length:myMat.elemSize() * myMat.total()];
    
    CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceRGB();
    
    CGDataProviderRef provider = CGDataProviderCreateWithCFData((__bridge CFDataRef)data);
    CGImageRef imageRef = CGImageCreate(myMat.cols, myMat.rows, 8, 8 * myMat.elemSize(), myMat.step[0], colorSpace, kCGImageAlphaNone | kCGBitmapByteOrderDefault, provider, NULL, false, kCGRenderingIntentDefault);
    
    UIImage *image = [[UIImage alloc]initWithCGImage:imageRef];
    CGImageRelease(imageRef);
    CGDataProviderRelease(provider);
    CGColorSpaceRelease(colorSpace);
    
    return image;
}


#if __cplusplus
- (void)processImage:(cv::Mat &)image;
{
    
    cvtColor(image, myMat, CV_RGBA2BGR);
    dispatch_async(dispatch_get_global_queue(0, 0), ^{
        dispatch_async(dispatch_get_main_queue(), ^{
            [imageView setImage:[ViewController imageWithCVMat]];
        });
    });
    
    
    /*
    circle(image, cv::Point(50, 50), 50, Scalar(0, 255, 0));
//    circle(image, cv::Point(50, 50), 50, Scalar(0, 255, 0));
    Mat processMat = image.clone();
    
    pyrDown(processMat, processMat);
    pyrDown(processMat, processMat);
    
    CMarkerRcgn mr;
    mr.setParam(processMat, markerMat);
    mr.doProc();
    
    DisPoint[0].x = 0;
    
    if(mr.m_point[0].x != 0){
        dispatch_async(dispatch_get_global_queue(0, 0), ^{
            dispatch_async(dispatch_get_main_queue(), ^{
                    [self.markerRecogView setBackgroundColor:[UIColor greenColor]];
            });
        });
        
        Scalar color(0, 255, 0);
    
        for(int i=0; i<4; i++){
            DisPoint[i].x = mr.m_point[i].x*4;
            DisPoint[i].y = mr.m_point[i].y*4;
            circle(image, DisPoint[i], 5, color, CV_FILLED);
        }
    }
    else{
        dispatch_async(dispatch_get_global_queue(0, 0), ^{
            dispatch_async(dispatch_get_main_queue(), ^{
                [self.markerRecogView setBackgroundColor:[UIColor redColor]];
            });
        });
    }
     */
}
#endif

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)pushCloseBt:(id)sender;
{
    NSString *appUrl = @"call://test_page/one?token=1234";
    [[UIApplication sharedApplication]openURL:[NSURL URLWithString:appUrl]];
    
//    exit(0);
    //    [[NSThread mainThread] finalize];

}

- (IBAction)pushShotBt:(id)sender{
    [catcher Stop];
    [self.videoCamera stop];
    
}
@end
