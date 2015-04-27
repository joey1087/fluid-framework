//
//  FFViewFactoryRegistration.h
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 10/04/2014.
//  Copyright (c) 2014 Hans Sponberg. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreGraphics/CGGeometry.h>
#import <UIKit/UIKit.h>

#import "FFDataNotificationService.h"
#import "AttributedText.h"

@class FFTFluidApp;
@class FFTView;
@class FFTViewBuilderInfo;
@class FFView;
@class FFTModalView;

@interface FFRectSizeWithFont : NSObject

@property (nonatomic, assign) CGRect bounds;
@property (nonatomic, strong) NSAttributedString *attributedString;

@end

@interface FFViewFactoryRegistration : NSObject

+ (void)registerViews:(FFTFluidApp *)fluidApp;
+ (FFRectSizeWithFont *)computeHeightOfText:(FFTAttributedText *)text width:(float)maxWidth height:(float)maxHeight fontName:(NSString *)fontName fontSizeInUnits:(float)fontSizeInUnits minFontSizeInUnits:(float)minFontSizeInUnits maxFontSizeInUnits:(float)maxFontSizeInUnits defaultColor:(UIColor *)defaultColor;
+ (UIFont *)fontFor:(NSString *)name sizeInUnits:(float)sizeInUnits;
+ (void)addDataChangeObserverFor:(NSString *)prefix dataModelKey:(NSString *)dataModelKey listenerId:(NSString *)listenerId listenForChildren:(BOOL)listenForChildren block:(observerBlock)block blockDataRemoved:(observerBlockDataRemoved)blockRemoved;
+ (void)removeDataChangeObserverFor:(NSString *)listenerId;
+ (NSAttributedString *)createAttributedString:(FFTAttributedText *)attText size:(float)pointSize defaultColor:(UIColor *)defaultColor fontName:(NSString*)fontName;

@end

@interface FFTViewBuilderInfo : NSObject

@property (nonatomic, assign) CGRect bounds;
@property (nonatomic, assign) CGRect parentBounds;
@property (nonatomic, strong) NSString *listenerId;
@property (nonatomic, strong) NSString *dataModelKeyPrefix;
@property (nonatomic, weak) FFView *fluidView;
@property (nonatomic, strong) NSString *viewPath;
@property (nonatomic, strong) FFTModalView *modalView;
@property (nonatomic, assign) BOOL fromDataListener;
@property (nonatomic, weak) UITableView *tableView;
@property (nonatomic, assign) BOOL precompute;

@end

