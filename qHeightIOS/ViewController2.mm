//
//  ViewController2.m
//  test3
//
//  Created by Jin Lee on 2016. 3. 1..
//  Copyright © 2016년 Jin Lee. All rights reserved.
//

#import "ViewController2.h"

@implementation ViewController2

CGPoint prevPoint;
float xPoint, yPoint;
CGRect prevRect;
float markerDist = 15;


@synthesize sliderRight1, sliderRight2, sliderLeft1, sliderLeft2, dateLabel, userDistance, realHeight, saveBt;
NSString *saveFileName;
-(IBAction)saveBt:(id)sender{
//    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
//    NSString *imagePath = [paths objectAtIndex:0];
//    NSString *filename = [NSString stringWithFormat:@"%@/%@.png", imagePath, saveFileName];
//
//    NSData* data = UIImagePNGRepresentation(imgView.image);
//    [data writeToFile:filename atomically:YES];
    UIImageWriteToSavedPhotosAlbum(imgView.image, nil, nil, nil);
    NSString *appUrl = [NSString stringWithFormat: @"call://test_page/%@" , self.realHeight.text];
//    NSString *appUrl = @"call://test_page/one?token=12.34&domain=/var/appdat/dsdf/few/tesfd/pdsf.png";
    [[UIApplication sharedApplication]openURL:[NSURL URLWithString:appUrl]];


}

- (void)setDate:(UILabel*)datelabel{
    
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc]init];
    [dateFormatter setDateFormat:@"yyyy"];
    int year = [[dateFormatter stringFromDate:[NSDate date]]intValue] % 100;
    [dateFormatter setDateFormat:@"MM"];
    int month = [[dateFormatter stringFromDate:[NSDate date]]intValue];
    [dateFormatter setDateFormat:@"dd"];
    int day = [[dateFormatter stringFromDate:[NSDate date]]intValue];
    
    [datelabel setText: [NSString stringWithFormat: @"%02d년%02d월%02d일", year, month, day]];
    saveFileName = [NSString stringWithFormat:@"%02d%02d%02d", year, month, day];
    
}

- (void)addDoneButton{
    UIToolbar* keyboardToolbar = [[UIToolbar alloc] init];
    [keyboardToolbar sizeToFit];
    UIBarButtonItem *flexBarButton = [[UIBarButtonItem alloc]
                                      initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace
                                      target:nil action:nil];
    UIBarButtonItem *doneBarButton = [[UIBarButtonItem alloc]
                                      initWithBarButtonSystemItem:UIBarButtonSystemItemDone
                                      target:self.view action:@selector(endEditing:)];
    keyboardToolbar.items = @[flexBarButton, doneBarButton];
    userDistance.inputAccessoryView = keyboardToolbar;
}

- (void)viewDidLoad{
    
    [[self sliderRight1]setUserInteractionEnabled:YES];
    [[self sliderRight2]setUserInteractionEnabled:YES];
    [[self sliderLeft1]setUserInteractionEnabled:YES];
    [[self sliderLeft2]setUserInteractionEnabled:YES];
    [imgView setImage:[ViewController2 imageWithCVMat]];
     NSLog(@"!!!!%d %d %f %f", markerAddMat.cols, markerAddMat.rows, imgView.frame.size.height, imgView.frame.size.width);

    if(DisPoint[0].x != 0){
        [[self sliderLeft1]removeFromSuperview];
        [[self sliderLeft2]removeFromSuperview];
        userDistance.text = [NSString stringWithFormat:@"%.1f", markerDist];
    }
    
    [self setDate:self.dateLabel];
    [self addDoneButton];
}


- (IBAction)pushCloseBt:(id)sender
{
    [self.navigationController popViewControllerAnimated:YES];
}

cv::Mat markerAddMat;

+(UIImage* )imageWithCVMat
{
    markerAddMat = myMat.clone();
//    cv::pyrUp(markerAddMat, markerAddMat);
//    cv::pyrUp(markerAddMat, markerAddMat);
    NSLog(@"MatSize %d %d", markerAddMat.rows, markerAddMat.cols);
    if(DisPoint[0].x !=0){
        
        cv::Scalar color(0, 255, 0);
        
        for(int i=0; i<4; i++){
            circle(markerAddMat, DisPoint[i], 5, color, CV_FILLED);
        }
    }
    
    
    NSData *data = [NSData dataWithBytes:markerAddMat.data length:markerAddMat.elemSize() * markerAddMat.total()];
    
    CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceRGB();
    
    CGDataProviderRef provider = CGDataProviderCreateWithCFData((__bridge CFDataRef)data);
    CGImageRef imageRef = CGImageCreate(markerAddMat.cols, markerAddMat.rows, 8, 8 * markerAddMat.elemSize(), markerAddMat.step[0], colorSpace, kCGImageAlphaNone | kCGBitmapByteOrderDefault, provider, NULL, false, kCGRenderingIntentDefault);
    
    UIImage *image = [[UIImage alloc]initWithCGImage:imageRef];
    CGImageRelease(imageRef);
    CGDataProviderRelease(provider);
    CGColorSpaceRelease(colorSpace);
    
    return image;
}

-(void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
    UITouch* bTouch = [touches anyObject];
    if([bTouch.view isKindOfClass:[UIImageView class]]){
        prevRect = bTouch.view.frame;
        prevPoint = [bTouch locationInView:[self view]];
        xPoint = prevPoint.x;
        yPoint = prevPoint.y - [[bTouch view]center].y;
    }
}



-(double)getHeight
{
    double retVal;
    double userDist = userDistance.text.doubleValue;
    double userY;
    if(DisPoint[0].x == 0){
        userY = sliderLeft2.center.y - sliderLeft1.center.y;
    }
    else{
        double userdx = DisPoint[0].x - DisPoint[3].x;
        double userdy = DisPoint[0].y - DisPoint[3].y;

        CGRect screenRect = imgView.bounds;
        CGFloat scrWidth = screenRect.size.width;
        CGFloat scrHeight = screenRect.size.height;
        
        userdx *= scrWidth;
        userdy *= scrHeight;
        
        userdx /= 720;
        userdy /= 1280;
        
        NSLog(@"!@!@%f %f", scrHeight, scrWidth);
        
        userY = sqrt(userdx * userdx + userdy * userdy);
    }
    
    NSLog(@"%lf", userY);
    
    if(userY < 0)
        userY = -userY;
    double objectY =  sliderRight2.center.y - sliderRight1.center.y;
    if(objectY < 0)
        objectY = -objectY;
    retVal = objectY * userDist / userY;
    
    return retVal;
}

-(void)touchesMoved:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
    UITouch* mTouch = [touches anyObject];
    if([mTouch.view isKindOfClass:[UIImageView class]]){
        CGPoint cp = [mTouch locationInView:[self view]];
        prevRect.origin.y = cp.y - yPoint;
        [[mTouch view]setFrame:prevRect];

    }
    
    
}

-(void)touchesEnded:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
    UITouch* mTouch = [touches anyObject];
    if([mTouch.view isKindOfClass:[UIImageView class]]){
        [[mTouch view]setTranslatesAutoresizingMaskIntoConstraints:YES];
        [[mTouch view]setFrame:prevRect];
        self.realHeight.text = [NSString stringWithFormat:@"%.2lf", [self getHeight]];
    }
}

-(void)didReceiveMemoryWarning{
    
}

@end