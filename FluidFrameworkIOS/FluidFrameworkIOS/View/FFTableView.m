//
//  FFTableView.m
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 2/05/2014.
//  Copyright (c) 2014 FluidFramework.org. All rights reserved.
//

#import "FFTableView.h"
#import "FFViewFactoryRegistration.h"
#import "GlobalState.h"
#import "FluidApp.h"
#import "FluidViewFactory.h"
#import "FFFluidAppDelegate.h"
#import "FFDataNotificationService.h"
#import "FFTableViewDelegate.h"

@interface FFTableView ()

@property (nonatomic, readwrite, strong) UITextField *activeField;
@property (nonatomic, readwrite, assign) BOOL keyboardShowing;
@property (nonatomic, readwrite, assign) CGSize keyboardSize;
@property (nonatomic, readwrite, assign) BOOL hasRunCleanup;

@end

@implementation FFTableView

- (id)initWithFrame:(CGRect)frame style:(UITableViewStyle)style {
    self = [super initWithFrame:frame style:style];
    if (self) {
        [self registerForKeyboardNotifications];
    }
    return self;
}

- (void)cleanup {
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    
    self.hasRunCleanup = YES;
}

- (void)registerForKeyboardNotifications {
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardWasShown:)
                                                 name:UIKeyboardDidShowNotification object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardWillBeHidden:)
                                                 name:UIKeyboardWillHideNotification object:nil];
    
}

- (void)keyboardWasShown:(NSNotification*)aNotification {
    self.keyboardShowing = YES;
    NSDictionary* info = [aNotification userInfo];
    self.keyboardSize = [[info objectForKey:UIKeyboardFrameBeginUserInfoKey] CGRectValue].size;
    [self makeFieldVisible];
}

- (void)makeFieldVisible {
    UIEdgeInsets contentInsets = UIEdgeInsetsMake(0.0, 0.0, self.keyboardSize.height, 0.0);
    self.contentInset = contentInsets;
    self.scrollIndicatorInsets = contentInsets;
    
    CGRect fieldRect = [self.window convertRect:self.activeField.frame fromView:self.activeField];
    
    CGRect aRect = self.frame;
    aRect.origin = CGPointMake(aRect.origin.x, aRect.origin.y + self.contentOffset.y);
    aRect.size.height -= self.keyboardSize.height;
    if (!CGRectContainsPoint(aRect, fieldRect.origin) ) {
        [self scrollRectToVisible:fieldRect animated:YES];
    }
}

- (void)keyboardWillBeHidden:(NSNotification*)aNotification {
    self.keyboardShowing = NO;
    UIEdgeInsets contentInsets = UIEdgeInsetsZero;
    self.contentInset = contentInsets;
    self.scrollIndicatorInsets = contentInsets;
}

- (void)textFieldDidBeginEditing:(UITextField *)textField {
    self.activeField = textField;
    if (self.keyboardShowing) {
        [self makeFieldVisible];
    }
}

- (void)textFieldDidEndEditing:(UITextField *)textField {
    self.activeField = nil;
}

- (void)willMoveToSuperview:(UIView *)newSuperview {
    
    if (self.hasRunCleanup) {
        return;
    }
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        dispatch_sync(dispatch_get_main_queue(), ^{
            if (newSuperview) {
                id<FFFluidAppDelegate> appDelegate = (id<FFFluidAppDelegate>) [[UIApplication sharedApplication] delegate];
                [[appDelegate dataNotificationService] enableDataChangeObserverForId:self.dataModelListenerId];
            } else {
                id<FFFluidAppDelegate> appDelegate = (id<FFFluidAppDelegate>) [[UIApplication sharedApplication] delegate];
                [[appDelegate dataNotificationService] disableDataChangeObserverForId:self.dataModelListenerId];
            }
        });
    });
}

- (int)rowIndexOfObjectWithId:(long long int)objectId {
    return [self.fluidTableDelegate rowIndexOfObjectWithId:objectId];
}

- (int)rowIndexOfDeletedObjectWithId:(long long int)objectId {
    return [self.fluidTableDelegate rowIndexOfDeletedObjectWithId:objectId];
}

@end
