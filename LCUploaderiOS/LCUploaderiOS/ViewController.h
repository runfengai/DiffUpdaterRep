//
//  ViewController.h
//  InsIos
//
//  Created by FWJ on 16/11/29.
//  Copyright © 2016年 ugpass. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "PDRCore.h"
@interface ViewController : UIViewController<PDRCoreDelegate>
{
    BOOL _isFullScreen;
    UIView *_containerView;
    UIView *_statusBarView;
    UIStatusBarStyle _statusBarStyle;
}
-(UIColor*)getStatusBarBackground;

@end

