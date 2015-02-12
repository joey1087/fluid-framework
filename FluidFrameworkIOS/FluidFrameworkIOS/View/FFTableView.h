//
//  FFTableView.h
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 2/05/2014.
//  Copyright (c) 2014 FluidFramework.org. All rights reserved.
//

@class FFTableViewDelegate;

#import <UIKit/UIKit.h>

@interface FFTableView : UITableView

@property (nonatomic, strong) NSString *viewPath;

@property (nonatomic, strong) FFTableViewDelegate *fluidTableDelegate; // to retain the object

@property (nonatomic, assign) NSString *dataModelListenerId;
@property (nonatomic, assign) NSString *fluidType;

- (id)initWithFrame:(CGRect)frame style:(UITableViewStyle)style;

- (int)rowIndexOfObjectWithId:(long long int)objectId;

- (int)rowIndexOfDeletedObjectWithId:(long long int)objectId;

- (void)cleanup;

@end
