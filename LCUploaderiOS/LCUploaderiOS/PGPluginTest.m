//
//  PGPluginTest.m
//  LCUploaderiOS
//
//  Created by FWJ on 17/2/9.
//  Copyright © 2017年 lc. All rights reserved.
//

#import "PGPluginTest.h"
#import "PDRCoreAppFrame.h"
#import "H5WEEngineExport.h"
#import "PDRToolSystemEx.h"
#import "SSZipArchive.h"
// 扩展插件中需要引入需要的系统库
#import <LocalAuthentication/LocalAuthentication.h>

#define kCache NSSearchPathForDirectoriesInDomains(NSCachesDirectory,NSUserDomainMask,YES).lastObject
#define kLibrary NSSearchPathForDirectoriesInDomains(NSLibraryDirectory, NSUserDomainMask, YES).lastObject

@implementation PGPluginTest
{
    NSFileManager *manager;
    BOOL isSuccess;
    NSString *oldPath;
    NSString *imgPath;
    NSString *imagePath;
    NSString *folderPath;}

- (void)upZipFile:(PGMethod*)commands
{
    if ( commands ) {

        // CallBackid 异步方法的回调id，H5+ 会根据回调ID通知JS层运行结果成功或者失败
        NSString* cbId = [commands.arguments objectAtIndex:0];

        // 用户的参数会在第二个参数传回，可以按照Array方式传入，
        NSArray* pArray = commands.arguments;

        //zip包路径
        NSString *zipPath = [NSString stringWithFormat:@"%@/Pandora/%@", kCache, [pArray objectAtIndex:1]];

        //解压到的路径
        folderPath = [NSString stringWithFormat:@"%@/Pandora/downloads/www", kCache];
        //解压
        [SSZipArchive unzipFileAtPath:zipPath toDestination:folderPath uniqueId:@"unzipFile"];
        //旧的
        NSString *appID = @"H5C22DBD6";
        oldPath = [NSString stringWithFormat:@"%@/Pandora/apps/%@/www",kLibrary, appID];
        imgPath = [NSString stringWithFormat:@"%@/images", oldPath];
        //新的图片文件夹
        imagePath = [NSString stringWithFormat:@"%@/images", folderPath];
        manager = [NSFileManager defaultManager];

        isSuccess = [self replace];
        NSArray * arr = [[NSArray alloc]init];
        arr = isSuccess ? [NSArray arrayWithObjects:@"1",@"更新成功", nil] : [NSArray arrayWithObjects:@"0",@"更新失败", nil];


        // 运行Native代码结果和预期相同，调用回调通知JS层运行成功并返回结果
        PDRPluginResult *result = [PDRPluginResult resultWithStatus:PDRCommandStatusOK messageAsString:arr];


        [self toCallback:cbId withReslut:[result toJSONString]];
    }
}

- (BOOL)replace{
    //将图片文件夹移动到新的文件夹下
    BOOL isMove = [manager moveItemAtPath:imgPath toPath:imagePath error:nil];
    if (!isMove) {
        return NO;
    }

    //        把旧的文件夹删除
    BOOL isDelete = [manager removeItemAtPath:oldPath error:nil];
    if (!isDelete) {
        return NO;
    }

    //把新的挪过来
    isMove = [manager moveItemAtPath:folderPath toPath:oldPath error:nil];
    if (!isMove) {
        return NO;
    }
    NSLog(@"folderPath=======%@",folderPath);

    return YES;
}
@end
