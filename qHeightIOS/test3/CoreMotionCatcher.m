//
//  CoreMotionCatcher.m
//  test3
//
//  Created by Jin Lee on 2016. 2. 29..
//  Copyright © 2016년 Jin Lee. All rights reserved.
//

#import "CoreMotionCatcher.h"


@interface CoreMotionCatcher ()

@property (strong, nonatomic) CMMotionManager *motionManager;

@end

@implementation CoreMotionCatcher

@synthesize myview, sensorRecogView;

CAShapeLayer *preLayer = nil;
-(void)Stop{
    [self.motionManager stopAccelerometerUpdates ];
    preLayer = nil;
}
-(void)Start:(UIView*)View sensorView:(UIView*)sensorview
{
    preLayer = nil;
    self.myview = View;
    self.sensorRecogView = sensorview;
    NSLog(@"Start CoreMotionCather");
    
    // Do any additional setup after loading the view, typically from a nib.
    
    self.motionManager = [[CMMotionManager alloc] init];
    self.motionManager.accelerometerUpdateInterval = .02;
    self.motionManager.gyroUpdateInterval = .2;
    
    [self.motionManager startAccelerometerUpdatesToQueue:[NSOperationQueue currentQueue]
                                             withHandler:^(CMAccelerometerData  *accelerometerData, NSError *error) {
                                                 [self outputAccelertionData:accelerometerData.acceleration];
                                                 if(error){
                                                     NSLog(@"%@", error);
                                                 }
                                             }];
    
}

-(void)outputAccelertionData:(CMAcceleration)acceleration
{
    CAShapeLayer *circleLayer = [CAShapeLayer layer];
    [circleLayer setPath:[[UIBezierPath bezierPathWithOvalInRect:CGRectMake(acceleration.x*20 + 18, acceleration.y*20+18, 6, 6)]CGPath]];

    if(acceleration.y >= -0.075 && acceleration.y <= 0.075){
        self.sensorRecogView.backgroundColor = [UIColor greenColor];
    }
    else{
        self.sensorRecogView.backgroundColor = [UIColor redColor];
    }
    
    [[self.myview layer]removeAllAnimations];
    if(preLayer == nil)
    {
        [[self.myview layer]addSublayer:circleLayer];
    }
    else{
        [[self.myview layer]replaceSublayer:preLayer with:circleLayer];
    }
    preLayer = circleLayer;
}


         
@end

