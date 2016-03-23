//
//  ViewController2.h
//  test3
//
//  Created by Jin Lee on 2016. 3. 1..
//  Copyright © 2016년 Jin Lee. All rights reserved.
//

#ifndef ViewController2_h
#define ViewController2_h

#import <UIKit/UIKit.h>
#import <opencv2/opencv.hpp>

extern cv::Mat myMat;
extern CvPoint DisPoint[4];


@interface ViewController2 : UIViewController{
    IBOutlet UIImageView *sliderRight1;
    IBOutlet UIImageView *sliderRight2;
    IBOutlet UIImageView *sliderLeft1;
    IBOutlet UIImageView *sliderLeft2;
    IBOutlet UIImageView *imgView;
    IBOutlet UIButton* closeBt;
    IBOutlet UILabel* dateLabel;
    IBOutlet UIButton* saveBt;
    IBOutlet UITextField* userDistance;
    IBOutlet UILabel* realHeight;
    
}

@property (nonatomic) IBOutlet UIImageView *sliderRight1, *sliderRight2, *sliderLeft1, *sliderLeft2;
@property (nonatomic) IBOutlet UILabel *dateLabel, *realHeight;
@property (nonatomic) IBOutlet UITextField *userDistance;
@property (nonatomic) IBOutlet UIButton *saveBt;

-(void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event;
-(void)touchesMoved:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event;
-(void)touchesEnded:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event;
- (IBAction)pushCloseBt:(id)sender;
-(IBAction)saveBt:(id)sender;

@end

#endif /* ViewController2_h */
