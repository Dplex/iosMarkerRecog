//
//  CoreMotionCatcher.h
//  test3
//
//  Created by Jin Lee on 2016. 2. 29..
//  Copyright © 2016년 Jin Lee. All rights reserved.
//

#ifndef CoreMotionCatcher_h
#define CoreMotionCatcher_h

#import <UIKit/UIKit.h>
#import <CoreMotion/CoreMotion.h>
#import <QuartzCore/QuartzCore.h>

@interface CoreMotionCatcher : UIView
{
    UIView* myview;
    UIView* sensorRecogView;
}

@property (nonatomic) IBOutlet UIView *myview;
@property (nonatomic) IBOutlet UIView *sensorRecogView;

-(void)Start:(UIView*)View sensorView:(UIView*)sensorview;
-(void)outputAccelertionData:(CMAcceleration)acceleration;
-(void)Stop;




@end
#endif /* CoreMotionCatcher_h */
