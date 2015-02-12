//
//  FFDataNotificationService.h
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 4/03/2014.
//  Copyright (c) 2014 Hans Sponberg. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef void (^observerBlock)(NSString *, NSArray *);

typedef void (^observerBlockDataRemoved)(NSString *);

@interface FFDataNotificationService : NSObject

- (void)addDataChangeObserverFor:(NSString *)prefix key:(NSString *)key observerId:(NSString *)observerId listenForChildren:(BOOL)listenForChildren block:(observerBlock)block blockDataRemoved:(observerBlockDataRemoved)blockDataRemoved;

- (void)enableDataChangeObserverForId:(NSString *)observerId;

- (void)disableDataChangeObserverForId:(NSString *)observerId;

- (void)removeDataChangeObserverFor:(NSString *)observerId;

@end
