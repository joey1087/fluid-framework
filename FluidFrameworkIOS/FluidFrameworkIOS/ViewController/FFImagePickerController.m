//
//  FFImagePickerController.m
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 9/04/2014.
//  Copyright (c) 2014 Hans Sponberg. All rights reserved.
//

#import "FFImagePickerController.h"

@interface FFImagePickerController ()

@property (nonatomic, readwrite, strong) FFTModalView *modalView;

@end

@implementation FFImagePickerController

- (id)initWithModalView:(FFTModalView *)modalView {
    self = [super init];
    if (self) {
        self.modalView = modalView;
    }
    return self;
}

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

@end
