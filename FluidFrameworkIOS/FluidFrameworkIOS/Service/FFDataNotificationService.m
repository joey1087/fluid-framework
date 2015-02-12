//
//  FFDataNotificationService.m
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 4/03/2014.
//  Copyright (c) 2014 Hans Sponberg. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "FFDataNotificationService.h"
#import "FFFluidAppDelegate.h"
#import "DataChangeListener.h"
#import "FluidApp.h"
#import "DataModelManager.h"
#import "GlobalState.h"

@interface FFTDataNotificationServicePair : NSObject<FFTDataChangeListener>

@property (nonatomic, strong) NSString *observerId;
@property (nonatomic, strong) observerBlock block;
@property (nonatomic, strong) observerBlockDataRemoved blockDataRemoved;

@end

@implementation FFTDataNotificationServicePair

- (void)dataChangedWithNSString:(NSString *)key withNSStringArray:(IOSObjectArray *)subKeys {
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        dispatch_sync(dispatch_get_main_queue(), ^{
            NSMutableArray *array = [NSMutableArray arrayWithCapacity:[subKeys count]];
            for (id object in subKeys) {
                [array addObject:object];
            }
            self.block(key, array);
        });
    });
}

- (void)dataRemovedWithNSString:(NSString *)key {

    if (self.blockDataRemoved) {
        self.blockDataRemoved(key);
    }
}

@end

@implementation FFDataNotificationService

- (id)init {
    if (self = [super init]) {
    }
    return self;
}

- (void)addDataChangeObserverFor:(NSString *)prefix key:(NSString *)key observerId:(NSString *)observerId listenForChildren:(BOOL)listenForChildren block:(observerBlock)block blockDataRemoved:(observerBlockDataRemoved)blockDataRemoved {
    
    FFTDataNotificationServicePair *pair = [[FFTDataNotificationServicePair alloc] init];
    pair.observerId = observerId;
    pair.block = block;
    pair.blockDataRemoved = blockDataRemoved;

    [[[FFTGlobalState fluidApp] getDataModelManager] addDataChangeListenerWithNSString:prefix withNSString:key withNSString:observerId withBoolean:listenForChildren withFFTDataChangeListener:pair];
}

- (void)enableDataChangeObserverForId:(NSString *)observerId {
    [[[FFTGlobalState fluidApp] getDataModelManager] enableDataChangeListenerWithNSString:observerId];
}

- (void)disableDataChangeObserverForId:(NSString *)observerId {
    [[[FFTGlobalState fluidApp] getDataModelManager] disableDataChangeListenerWithNSString:observerId];
}

- (void)removeDataChangeObserverFor:(NSString *)observerId {
    [[[FFTGlobalState fluidApp] getDataModelManager] removeDataChangeListenerWithNSString:observerId];
}

@end
