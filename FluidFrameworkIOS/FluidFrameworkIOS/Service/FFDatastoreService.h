//
//  FFDatastoreService.h
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 22/04/2014.
//  Copyright (c) 2014 FluidFramework.org. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <sqlite3.h>

#import "DatastoreService.h"

@interface FFDatastoreService : NSObject<FFTDatastoreService> {
    sqlite3 *_database;
}

@end
