//
//  FFNavigationViewController.h
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 10/04/2014.
//  Copyright (c) 2014 Hans Sponberg. All rights reserved.
//

#import <UIKit/UIKit.h>

#define ACTION_SHEET_PHOTO_OR_LIBRARY 1

@interface FFNavigationViewController : UINavigationController<UIImagePickerControllerDelegate, UINavigationControllerDelegate, UIActionSheetDelegate>

@end
