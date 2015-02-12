//
//  FFTableViewDelegate.m
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 2/20/14.
//  Copyright (c) 2014 Hans Sponberg. All rights reserved.
//

#import "FFTableViewDelegate.h"
#import "View.h"
#import "ViewBehaviorTable.h"
#import "java/util/ArrayList.h"
#import "java/lang/Double.h"
#import "java/lang/Integer.h"
#import "TableRow.h"
#import "FluidApp.h"
#import "Layout.h"
#import "FFView.h"
#import "FFFluidAppDelegate.h"
#import "FFDataNotificationService.h"
#import "GlobalState.h"
#import "TableLayout.h"
#import "ViewManager.h"
#import "FFTableView.h"
#import "EventsManager.h"
#import "ActionListener.h"
#import "ViewPosition.h"
#include "java/lang/Long.h"
#include "UIService.h"

#include "com/sponberg/fluid/layout/DataModelManager.h"

@interface FFTableViewDelegate ()

@property (nonatomic, assign) FFTViewPosition *view;
@property (nonatomic, strong) FFTLRUCache *cache;
@property (nonatomic, strong) FFTLRUCache *headerCache;
@property (nonatomic, assign) BOOL hasRanCleanup;

@end

@implementation FFTableViewDelegate

- (id)initWithView:(FFTViewPosition *)view {
    if (self = [super init]) {
        self.view = view;
        self.cache = [[FFTLRUCache alloc] initWithInt:100];
        self.headerCache = [[FFTLRUCache alloc] initWithInt:10];

        FFTViewBehaviorTable *viewBehavior = (FFTViewBehaviorTable *) self.view->viewBehavior_;
        
        _rowHeight = 0;
        
        double rowHeight = [viewBehavior getRowHeight].doubleValue;
        if (rowHeight) {
            _rowHeight = [[FFTGlobalState fluidApp] unitsToPixelsWithDouble:rowHeight];
        }
    }
    return self;
}


/*
- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 1;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return 1;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    // Return the number of sections.
    return 2;
}

-(UIView*)tableView:(UITableView*)tableView viewForHeaderInSection:(NSInteger)section
{
    return [[UIView alloc] initWithFrame:CGRectZero];
}

-(UIView*)tableView:(UITableView*)tableView viewForFooterInSection:(NSInteger)section
{
    return [[UIView alloc] initWithFrame:CGRectZero];
}
*/

-(CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)sectionIndex {
    FFTViewBehaviorTable *viewBehavior = (FFTViewBehaviorTable *) self.view->viewBehavior_;
    
    FFTTableLayout_TableSection *section = [[viewBehavior getRowProvider] getSectionAtWithInt:sectionIndex];
    if (section) {
        FFTLayout *layout = [[FFTGlobalState fluidApp] getLayoutWithNSString:[section getSectionHeaderLayout]];
        return [self heightForSectionLayout:layout width:tableView.frame.size.width dataModelPrefix:nil];
    } else {
        return [[viewBehavior getSectionHeaderHeight] floatValue];
    }
}

-(UIView*)tableView:(UITableView*)tableView viewForHeaderInSection:(NSInteger)sectionIndex {
    FFTViewBehaviorTable *viewBehavior = (FFTViewBehaviorTable *) self.view->viewBehavior_;
    FFTTableLayout_TableSection *section = [[viewBehavior getRowProvider] getSectionAtWithInt:sectionIndex];
    FFTLayout *layout = [[FFTGlobalState fluidApp] getLayoutWithNSString:[section getSectionHeaderLayout]];
    if (section && layout) {
        
        NSString *key = [NSString stringWithFormat:@"%d", sectionIndex];
        FFView *view = [self.headerCache getWithId:key];
        float height = [self heightForSectionLayout:layout width:tableView.frame.size.width dataModelPrefix:nil];
        CGRect bounds = CGRectMake(0, 0, tableView.frame.size.width, height);
        if (!view) {
            
            FFTableView *ffTableView = (FFTableView *)tableView;
            NSString *viewPath = layout->id__;
            int i = [viewPath rangeOfString:@"." options:NSBackwardsSearch].location;
            if (i != NSNotFound) {
                viewPath = [viewPath substringFromIndex:(i + 1)];
            }
            viewPath = [NSString stringWithFormat:@"%@.%@", ffTableView.viewPath, viewPath];
            
            view = [[FFView alloc] initWithFrame:bounds viewPath:viewPath layout:layout rootFFView:[FFView rootFFView:tableView] inTableView:tableView];
            [self.headerCache putWithId:key withId:view];
            view.listenToDataModelChanges = NO;
        }
        [view createOrUpdateViews:bounds fromDataListener:NO];
        return view;
    } else {
        return nil;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return 0;
}

-(UIView*)tableView:(UITableView*)tableView viewForFooterInSection:(NSInteger)section {
    return [[UIView alloc] initWithFrame:CGRectZero];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    FFTViewBehaviorTable *viewBehavior = (FFTViewBehaviorTable *) self.view->viewBehavior_;
    return [[viewBehavior getRowProvider] getNumSections];
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    if (_rowHeight != 0) {
        return _rowHeight;
    }
    
    FFTViewBehaviorTable *viewBehavior = (FFTViewBehaviorTable *) self.view->viewBehavior_;
    
    int section = [indexPath section];
    
    FFTTableRow *row = [[viewBehavior getRowProvider] getRowInSectionAtWithInt:section withInt:[indexPath row]];
    
    FFTLayout *layout = [[FFTGlobalState fluidApp] getLayoutWithNSString:row->layout_];
    
    long long itemId = [self itemId:[indexPath section] rowIndex:[indexPath row]];
    return [self heightForLayout:layout width:tableView.frame.size.width dataModelPrefix:[row getKey] objectId:itemId];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {

    FFTViewBehaviorTable *viewBehavior = (FFTViewBehaviorTable *) self.view->viewBehavior_;

    return [[viewBehavior getRowProvider] getNumRowsInSectionWithInt:section];
}

- (long long)itemId:(int)sectionIndex rowIndex:(int)rowIndex {
    FFTViewBehaviorTable *viewBehavior = (FFTViewBehaviorTable *) self.view->viewBehavior_;
    return [[viewBehavior getRowProvider] getItemIdAtWithInt:sectionIndex withInt:rowIndex];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    tableView.userInteractionEnabled = YES;
    
    FFTableView *ffTableView = (FFTableView *)tableView;
    
    FFTViewBehaviorTable *viewBehavior = (FFTViewBehaviorTable *) self.view->viewBehavior_;
    
    FFTTableRow *row = [[viewBehavior getRowProvider] getRowInSectionAtWithInt:[indexPath section] withInt:[indexPath row]];
    
    FFTLayout *layout = [[FFTGlobalState fluidApp] getLayoutWithNSString:row->layout_];

    long long itemId = [self itemId:[indexPath section] rowIndex:[indexPath row]];
    
    NSString *dataModelListenerId = [NSString stringWithFormat:@"%@%lld", ffTableView.dataModelListenerId, itemId];
    
    //[layout setDataModelIdWithNSString:row->dataModelId_];
    
    if (layout == nil) {
        [NSException raise:@"Layout is null" format:@"Layout is null %@", row->layout_];
        return nil;
    }
    
    float width = tableView.bounds.size.width;
    float height = [self heightForLayout:layout width:tableView.frame.size.width dataModelPrefix:[row getKey] objectId:itemId];
    
    CGRect bounds = CGRectMake(0, 0, width, height);
    
    NSString *viewPath = layout->id__;
    int i = [viewPath rangeOfString:@"." options:NSBackwardsSearch].location;
    if (i != NSNotFound) {
        viewPath = [viewPath substringFromIndex:(i + 1)];
    }
    viewPath = [NSString stringWithFormat:@"%@.%@|%lld", ffTableView.viewPath, viewPath, itemId];
    
    FFView *view = [self.cache getWithId:viewPath];
    if (!view) {
        FFView *root = [FFView rootFFView:tableView];
        view = [[FFView alloc] initWithFrame:bounds viewPath:viewPath layout:layout rootFFView:root inTableView:tableView];
        view.listenToDataModelChanges = NO;
        view.dataModelListenerId = dataModelListenerId;
        [self.cache putWithId:viewPath withId:view];
    }
    
    //FFView *view;
    
    UITableViewCell* cell = [tableView dequeueReusableCellWithIdentifier:@"cell"];
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"cell"];
        cell.backgroundColor = [UIColor clearColor];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        //view = [[FFView alloc] initWithFrame:bounds viewId:ffTableView.viewId layout:layout userTappable:row rootFFView:root];
        [cell.contentView addSubview:view];
    } else {
        
        FFView *oldView = [[cell.contentView subviews] firstObject];
        //[oldView cleanup];
        [oldView removeFromSuperview];

        [cell.contentView addSubview:view];
    }
    
    if ([[[UIDevice currentDevice] systemVersion] floatValue] < 7.0) {
        cell.backgroundView = [UIView new];
    }
    
    NSString *selectedCondition = [layout getPropertyWithNSString:@"tablerow" withNSString:@"selected-condition"];
    
    if (selectedCondition && [[[FFTGlobalState fluidApp] getDataModelManager] checkConditionWithNSString:selectedCondition withNSString:[row getKey]]) {
        cell.accessoryType = UITableViewCellAccessoryCheckmark;
    } else {
        cell.accessoryType = UITableViewCellAccessoryNone;
    }
    
    if (!view.backgroundColor) {
        NSString *tableLayoutId = [viewBehavior getTableLayoutId];
        if (tableLayoutId) {
            FFTTableLayout *tableLayout = [[[FFTGlobalState fluidApp] getViewManager] getTableLayoutWithNSString:[viewBehavior getTableLayoutId]];
            if ([tableLayout getBackgroundColor]) {
                view.backgroundColor = [FFView color:[tableLayout getBackgroundColor]];
            }
        }
    }
    
    view.dataModelKeyPrefix = [row getKey];
    
    view.frame = bounds;
    cell.frame = bounds;

    [view createOrUpdateViews:bounds fromDataListener:NO];
    [view setTextFieldDelegate:tableView];
    
    return cell;
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    
    // TODO: can enable data notifications for this cell
}

- (void)tableView:(UITableView *)tableView didEndDisplayingCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    
    //FFView *view = [[cell.contentView subviews] firstObject];
    //[view cleanup];
    
    //[[appDelegate dataNotificationService] disableDataChangeObserverForId:[self.screen getScreenId]];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    FFTableView *view = (FFTableView *)tableView;

    FFTViewBehaviorTable *viewBehavior = (FFTViewBehaviorTable *) self.view->viewBehavior_;
    FFTTableRow *row = [[viewBehavior getRowProvider] getRowInSectionAtWithInt:[indexPath section] withInt:[indexPath row]];
    
    FFTActionListener_EventInfo *eventInfo = [[FFTActionListener_EventInfo alloc] init];
    [eventInfo setDataModelKeyParentWithNSString:[row getKey]];
    //[eventInfo setDataModelKeyWithNSString:[NSString stringWithFormat:@"%d", indexPath.row]];
    [eventInfo setDataModelKeyWithNSString:[NSString stringWithFormat:@"%lld", [row getId]]];
    //[eventInfo setUserInfoWithId:[NSString stringWithFormat:@"%lld", [row getId]]];
    //[eventInfo setUserInfoWithId:[NSNumber numberWithLongLong:[row getId]]];
    
    if ([viewBehavior getTableLayoutId]) {
        // Table Layout, use name of row
        NSArray *comps = [[row getLayout] componentsSeparatedByString:@"."];
        NSString *rowLayout = [comps lastObject];
        [eventInfo setUserInfoWithId:rowLayout];
    } else {
        // Use index of row
        [eventInfo setUserInfoWithId:[JavaLangLong valueOfWithLong:[row getId]]];
    }

    [[[FFTGlobalState fluidApp] getEventsManager] userTappedWithNSString:view.viewPath withFFTActionListener_EventInfo:eventInfo];
}

- (float)heightForSectionLayout:(FFTLayout *)layout width:(float)width dataModelPrefix:(NSString *)dataModelPrefix {
    
    float height;
    
    NSString *heightString = [layout getPropertyWithNSString:@"tablerow" withNSString:@"height"];
    if ([heightString isEqualToString:@"compute"]) {
        BOOL landscape = [[FFTGlobalState_fluidApp__ getUiService] isOrientationLandscape];
        height = [layout calculateHeightWithBoolean:landscape withFloat:width withNSString:dataModelPrefix];
    } else {
        height = [heightString floatValue];
        height = [[FFTGlobalState fluidApp] unitsToPixelsWithDouble:height];
    }
    
    return height;
}

- (float)heightForLayout:(FFTLayout *)layout width:(float)width dataModelPrefix:(NSString *)dataModelPrefix objectId:(long long)objectId {
    
    float height;
    
    NSString *heightString = [layout getPropertyWithNSString:@"tablerow" withNSString:@"height"];
    if ([heightString isEqualToString:@"compute"]) {
        BOOL landscape = [[FFTGlobalState_fluidApp__ getUiService] isOrientationLandscape];
        height = [layout calculateHeightWithBoolean:landscape withFloat:width withNSString:dataModelPrefix];
    } else if ([heightString isEqualToString:@"from-object"]) {
        FFTViewBehaviorTable *viewBehavior = (FFTViewBehaviorTable *) self.view->viewBehavior_;
        height = [viewBehavior getHeightFromObjectWithWithLong:objectId];
    } else {
        height = [heightString floatValue];
        height = [[FFTGlobalState fluidApp] unitsToPixelsWithDouble:height];
    }

    return height;
}

- (int)rowIndexOfObjectWithId:(long long int)objectId {
    
    FFTViewBehaviorTable *viewBehavior = (FFTViewBehaviorTable *) self.view->viewBehavior_;
    return [[viewBehavior getRowProvider] getRowIndexOfObjectWithLong:objectId];
}

- (int)rowIndexOfDeletedObjectWithId:(long long int)objectId {
    
    FFTViewBehaviorTable *viewBehavior = (FFTViewBehaviorTable *) self.view->viewBehavior_;
    return [[viewBehavior getRowProvider] getIndexOfRecentlyDeletedObjectWithLong:objectId];
}

- (BOOL)tableView:(UITableView *)tableView shouldIndentWhileEditingRowAtIndexPath:(NSIndexPath *)indexPath {
    return NO;
}

- (void)cleanup {

    if (self.hasRanCleanup) {
        return;
    }
    
    self.hasRanCleanup = YES;
    
    id<JavaUtilCollection> values = [self.cache values];
    for (FFView *view in values) {

        [view cleanup];
    }
    
    [self.headerCache clear];
    
    values = [self.cache values];
    for (FFView *view in values) {
        
        [view cleanup];
    }

    [self.cache clear];
}

@end
