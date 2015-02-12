//
//  FFAlertView.m
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 18/07/2014.
//  Copyright (c) 2014 FluidFramework.org. All rights reserved.
//

#include "com/sponberg/fluid/Callback.h"

#import "FFAlertView.h"
#import "ModalView.h"

@implementation FFAlertView

- (void)show {
    
    self.delegate = self;

    [super show];
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
 
    [self.callback runWithNSString:@"done"];
    
    if (self.modalView) {

        FFTModalView_ModalViewConfirmation *userData = [self.modalView getUserData];
        
        NSString *title = [alertView buttonTitleAtIndex:buttonIndex];
        title = [title lowercaseString];
        
        //if ([title isEqualToString:[userData getCancel]]) {
        //    [self.modalView modalCanceled];
        //} else {
            [self.modalView modalCompleteWithId:title];
        //}
    }
}

@end
