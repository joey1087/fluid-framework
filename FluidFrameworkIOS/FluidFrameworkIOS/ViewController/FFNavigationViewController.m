//
//  FFNavigationViewController.m
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 10/04/2014.
//  Copyright (c) 2014 Hans Sponberg. All rights reserved.
//

#import "FFNavigationViewController.h"
#import "FFImagePickerController.h"
#import "ModalView.h"
#include "java/util/HashMap.h"
#import "FFActionSheet.h"

@interface FFNavigationViewController ()

@end

@implementation FFNavigationViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info {
    
    FFImagePickerController *fPicker = (FFImagePickerController *)picker;
    FFTModalView *modalView = fPicker.modalView;
    JavaUtilHashMap *map = [modalView getUserData];
    
    NSString *format = [map getWithId:@"format"];
    
    UIImage *image = [info objectForKey:UIImagePickerControllerOriginalImage];
    
    CGSize newSize = CGSizeMake(image.size.width, image.size.height);
    
    int maxWidth = [[map getWithId:@"maxWidth"] intValue];
    int maxHeight = [[map getWithId:@"maxWidth"] intValue];
    
    if (maxWidth > 0 && maxWidth < newSize.width) {
        float scale = maxWidth * 1.0 / newSize.width;
        newSize.width = maxWidth;
        newSize.height = newSize.height * scale;
    }
    if (maxHeight > 0 && maxHeight < newSize.height) {
        float scale = maxHeight * 1.0 / newSize.height;
        newSize.height = maxHeight;
        newSize.width = newSize.width * scale;
    }
    
    if (newSize.width != image.size.width || newSize.height != image.size.height) {
        image = [self imageWithImage:image convertToSize:newSize];
    }
    
    NSData *bytes;
    if ([format isEqualToString:@"jpg"]) {
        
        float quality = 1;
        if ([map getWithId:@"quality"]) {
            quality = [[map getWithId:@"quality"] floatValue] / 100;
        }
        
        bytes = UIImageJPEGRepresentation(image, quality);
    } else {
        bytes = UIImagePNGRepresentation(image);
    }
    
    IOSByteArray *byteArray = [IOSByteArray arrayWithBytes:[bytes bytes] count:[bytes length]];
    
    [fPicker.modalView modalCompleteWithId:byteArray];
     
    // if save to camera roll
    // UIImageWriteToSavedPhotosAlbum(image, nil, nil, nil);
    
    [self dismissViewControllerAnimated:YES completion:^{}];
}

- (UIImage *)imageWithImage:(UIImage *)image convertToSize:(CGSize)size {
    UIGraphicsBeginImageContext(size);
    [image drawInRect:CGRectMake(0, 0, size.width, size.height)];
    UIImage *destImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return destImage;
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker {
    FFImagePickerController *fPicker = (FFImagePickerController *) picker;
    [fPicker.modalView modalCanceled];
    [self dismissViewControllerAnimated:YES completion:^{}];
}

- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex {
    
    FFActionSheet *fSheet = (FFActionSheet *)actionSheet;
    
    if (actionSheet.tag == ACTION_SHEET_PHOTO_OR_LIBRARY) {
        if (buttonIndex == 2) {
            [fSheet.modalView modalCanceled];
            return;
        }
        
        FFImagePickerController *picker = [[FFImagePickerController alloc] initWithModalView:fSheet.modalView];
        if (buttonIndex == 0) {
            picker.sourceType = UIImagePickerControllerSourceTypeCamera;
        } else if (buttonIndex == 1) {
            picker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
        }
        picker.delegate = self;
        
        [self presentViewController:picker animated:YES completion:^{}];
    }
}


@end
