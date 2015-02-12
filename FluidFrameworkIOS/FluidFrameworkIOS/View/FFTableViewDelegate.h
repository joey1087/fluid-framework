//
//  FFTableViewDelegate.h
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 2/20/14.
//  Copyright (c) 2014 Hans Sponberg. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h> 

@class FFTView, FFTViewPosition;

@interface FFTableViewDelegate : NSObject<UITableViewDelegate, UITableViewDataSource> {
    double _rowHeight;
}

- (id)initWithView:(FFTViewPosition *)view;

- (int)rowIndexOfObjectWithId:(long long int)objectId;

- (int)rowIndexOfDeletedObjectWithId:(long long int)objectId;

- (void)cleanup;

@end
