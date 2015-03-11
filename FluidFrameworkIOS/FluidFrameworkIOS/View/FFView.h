//
//  FFView.h
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 26/02/2014.
//  Copyright (c) 2014 Hans Sponberg. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "DataChangeListener.h"

typedef void (^tappedOutsideWhileFocusedListener)();

@class FFTLayout, FFTView, FFTColor, FFTViewBehavior, FFTViewPosition, FFTModalView;

@interface FFView : UIView<UITextFieldDelegate, FFTDataChangeListener> {
    CGSize keyboardSize;
}

@property (nonatomic, strong) NSMutableDictionary *viewsById;
@property (nonatomic, strong) FFTLayout *layout;
@property (nonatomic, strong) NSString *dataModelKeyPrefix;
@property (nonatomic, strong) NSString *viewPath;
@property (nonatomic, strong) NSString *dataModelListenerId;
@property (nonatomic, strong) FFTModalView *modalView;

@property (nonatomic, weak) FFView *rootFFView;
@property (nonatomic, assign) BOOL listenToDataModelChanges;
@property (nonatomic, weak) UITableView *tableView;
@property (nonatomic, assign) BOOL lastViewPathTokenIsIndex;

+ (id)rootFFView:(UIView *)view;

+ (NSString *)valueFor:(FFTViewPosition *)view baseText:(NSString *)baseText dataModelKeyPrefix:(NSString *)dataModelKeyPrefix;

+ (void)setValueFor:(FFTViewPosition *)view dataModelKeyPrefix:(NSString *)dataModelKeyPrefix to:(id)value;

+ (void)styleView:(UIView *)view viewBehavior:(FFTViewBehavior *)viewBehavior dataModelPrefix:(NSString *)dataModelPrefix;

+ (UIColor *)color:(FFTColor *)ffCol;

- (id)initWithFrame:(CGRect)frame viewPath:(NSString *)viewPath layout:(FFTLayout *)layout rootFFView:(FFView *)rootFFView inTableView:(UITableView *)tableView;

- (void)createOrUpdateViews:(CGRect)bounds fromDataListener:(BOOL)fromDataListener;

- (void)addTappedOutsideWhileFocusedListener:(UIView *)view tappedOutsideWhileFocusedListener:(tappedOutsideWhileFocusedListener)listener;

- (void)setTextFieldDelegate:(id)tfd;

- (void)cleanup;

- (void)textFieldDidBeginEditingHelper:(UIView *)textField;

- (void)textFieldDidEndEditingHelper:(UIView *)textField;

- (void)grabFocusForView:(NSString *)viewId;

- (void)hideKeyboard;

- (void)createOrLayoutViews;

- (void)setScrollViewContentSize;

- (void)scrollToBottomWithNSString:(NSString *)viewPath
                      withNSString:(NSString *)viewId;

@end
