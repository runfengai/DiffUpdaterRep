//
//  PGPluginTest.h
//  LCUploaderiOS
//
//  Created by FWJ on 17/2/9.
//  Copyright © 2017年 lc. All rights reserved.
//

#include "PGPlugin.h"
#include "PGMethod.h"
#import <Foundation/Foundation.h>



@interface PGPluginTest : PGPlugin

//- (void)PluginTestFunction:(PGMethod*)command;
- (void)upZipFile:(PGMethod*)command;

@end
