
//
//  VideoCamera.h
//  test3
//
//  Created by Jin Lee on 2016. 3. 3..
//  Copyright © 2016년 Jin Lee. All rights reserved.
//

#ifndef Header_h
#define Header_h

#import <opencv2/videoio/cap_ios.h>

@protocol VideoCameraDelegate <CvVideoCameraDelegate>
@end

@interface VideoCamera : CvVideoCamera

- (void)updateOrientation;
- (void)layoutPreviewLayer;

@end

#endif /* Header_h */
