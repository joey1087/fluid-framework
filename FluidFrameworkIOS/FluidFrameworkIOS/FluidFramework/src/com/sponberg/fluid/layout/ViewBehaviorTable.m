//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/fluid/layout/ViewBehaviorTable.java
//

#include "IOSClass.h"
#include "com/sponberg/fluid/layout/TableLayout.h"
#include "com/sponberg/fluid/layout/TableRow.h"
#include "com/sponberg/fluid/layout/ViewBehavior.h"
#include "com/sponberg/fluid/layout/ViewBehaviorTable.h"
#include "com/sponberg/fluid/util/KeyValueList.h"
#include "java/lang/Boolean.h"
#include "java/lang/Double.h"

@implementation FFTViewBehaviorTable

- (id)initWithFFTKeyValueList:(id<FFTKeyValueList>)properties
withFFTViewBehaviorTable_RowProvider:(id<FFTViewBehaviorTable_RowProvider>)rowProvider {
  if (self = [super initWithNSString:FFTViewBehavior_get_table_() withFFTKeyValueList:properties]) {
    scrollToTopWhenUpdate_ = NO;
    self->sectionFooterHeight_ = [FFTViewBehavior getUnitsToPixelsPropertyWithNSString:@"section-footer-height" withJavaLangDouble:nil withFFTKeyValueList:properties];
    self->sectionHeaderHeight_ = [FFTViewBehavior getUnitsToPixelsPropertyWithNSString:@"section-header-height" withJavaLangDouble:nil withFFTKeyValueList:properties];
    self->headerHeight_ = [FFTViewBehavior getDoublePropertyWithNSString:@"header-height" withJavaLangDouble:nil withFFTKeyValueList:properties];
    self->footerHeight_ = [FFTViewBehavior getDoublePropertyWithNSString:@"footer-height" withJavaLangDouble:nil withFFTKeyValueList:properties];
    self->scrollEnabled_ = [FFTViewBehavior getBooleanPropertyWithNSString:@"scroll-enabled" withBoolean:YES withFFTKeyValueList:properties];
    self->showsVerticalScrollIndicator_ = [FFTViewBehavior getBooleanPropertyWithNSString:@"shows-vertical-scroll-indicator" withBoolean:YES withFFTKeyValueList:properties];
    self->rowProvider_ = rowProvider;
    self->tableLayoutId_ = [FFTViewBehavior getStringPropertyWithNSString:@"table-layout" withNSString:nil withFFTKeyValueList:properties];
    self->rowHeight_ = [FFTViewBehavior getDoublePropertyWithNSString:@"row-height" withJavaLangDouble:nil withFFTKeyValueList:properties];
    self->showRowDivider_ = [((JavaLangBoolean *) nil_chk([FFTViewBehavior getBooleanPropertyWithNSString:@"show-row-divider" withBoolean:YES withFFTKeyValueList:properties])) booleanValue];
    self->stickyHeaders_ = [((JavaLangBoolean *) nil_chk([FFTViewBehavior getBooleanPropertyWithNSString:@"sticky-headers" withBoolean:NO withFFTKeyValueList:properties])) booleanValue];
    self->paddingBottom_ = [FFTViewBehavior getSizePropertyWithNSString:@"padding-bottom" withNSString:nil withFFTKeyValueList:properties];
    self->scrollToBottomOnLoad_ = [((JavaLangBoolean *) nil_chk([FFTViewBehavior getBooleanPropertyWithNSString:@"scroll-to-bottom-on-load" withBoolean:NO withFFTKeyValueList:properties])) booleanValue];
    self->scrollToTopWhenUpdate_ = [((JavaLangBoolean *) nil_chk([FFTViewBehavior getBooleanPropertyWithNSString:@"scroll-to-top-on-update" withBoolean:NO withFFTKeyValueList:properties])) booleanValue];
  }
  return self;
}

- (void)setRowProviderWithFFTViewBehaviorTable_RowProvider:(id<FFTViewBehaviorTable_RowProvider>)rowProvider {
  self->rowProvider_ = rowProvider;
}

- (id)getRowOrSectionAtWithInt:(int)index {
  return [((id<FFTViewBehaviorTable_RowProvider>) nil_chk(rowProvider_)) getRowOrSectionAtWithInt:index];
}

- (long long int)getItemIdWithInt:(int)index {
  return [((id<FFTViewBehaviorTable_RowProvider>) nil_chk(rowProvider_)) getItemIdAtWithInt:index];
}

- (int)getCount {
  return [((id<FFTViewBehaviorTable_RowProvider>) nil_chk(rowProvider_)) getCount];
}

- (double)getHeightFromObjectWithWithLong:(long long int)id_ {
  return [((id<FFTViewBehaviorTable_RowProvider>) nil_chk(rowProvider_)) getHeightFromObjectWithWithLong:id_];
}

- (NSString *)description {
  return [NSString stringWithFormat:@"ViewBehaviorTable(rowHeight=%@, sectionFooterHeight=%@, sectionHeaderHeight=%@, headerHeight=%@, footerHeight=%@, scrollEnabled=%@, showsVerticalScrollIndicator=%@, showRowDivider=%@, rowProvider=%@, tableLayoutId=%@, stickyHeaders=%@, paddingBottom=%@, scrollToBottomOnLoad=%@, scrollToTopWhenUpdate=%@)", [self getRowHeight], [self getSectionFooterHeight], [self getSectionHeaderHeight], [self getHeaderHeight], [self getFooterHeight], [self getScrollEnabled], [self getShowsVerticalScrollIndicator], [JavaLangBoolean toStringWithBoolean:[self isShowRowDivider]], [self getRowProvider], [self getTableLayoutId], [JavaLangBoolean toStringWithBoolean:[self isStickyHeaders]], [self getPaddingBottom], [JavaLangBoolean toStringWithBoolean:[self isScrollToBottomOnLoad]], [JavaLangBoolean toStringWithBoolean:[self isScrollToTopWhenUpdate]]];
}

- (JavaLangDouble *)getRowHeight {
  return self->rowHeight_;
}

- (JavaLangDouble *)getSectionFooterHeight {
  return self->sectionFooterHeight_;
}

- (JavaLangDouble *)getSectionHeaderHeight {
  return self->sectionHeaderHeight_;
}

- (JavaLangDouble *)getHeaderHeight {
  return self->headerHeight_;
}

- (JavaLangDouble *)getFooterHeight {
  return self->footerHeight_;
}

- (JavaLangBoolean *)getScrollEnabled {
  return self->scrollEnabled_;
}

- (JavaLangBoolean *)getShowsVerticalScrollIndicator {
  return self->showsVerticalScrollIndicator_;
}

- (BOOL)isShowRowDivider {
  return self->showRowDivider_;
}

- (id<FFTViewBehaviorTable_RowProvider>)getRowProvider {
  return self->rowProvider_;
}

- (NSString *)getTableLayoutId {
  return self->tableLayoutId_;
}

- (BOOL)isStickyHeaders {
  return self->stickyHeaders_;
}

- (JavaLangDouble *)getPaddingBottom {
  return self->paddingBottom_;
}

- (BOOL)isScrollToBottomOnLoad {
  return self->scrollToBottomOnLoad_;
}

- (BOOL)isScrollToTopWhenUpdate {
  return self->scrollToTopWhenUpdate_;
}

- (void)setRowHeightWithJavaLangDouble:(JavaLangDouble *)rowHeight {
  self->rowHeight_ = rowHeight;
}

- (void)setSectionFooterHeightWithJavaLangDouble:(JavaLangDouble *)sectionFooterHeight {
  self->sectionFooterHeight_ = sectionFooterHeight;
}

- (void)setSectionHeaderHeightWithJavaLangDouble:(JavaLangDouble *)sectionHeaderHeight {
  self->sectionHeaderHeight_ = sectionHeaderHeight;
}

- (void)setHeaderHeightWithJavaLangDouble:(JavaLangDouble *)headerHeight {
  self->headerHeight_ = headerHeight;
}

- (void)setFooterHeightWithJavaLangDouble:(JavaLangDouble *)footerHeight {
  self->footerHeight_ = footerHeight;
}

- (void)setScrollEnabledWithJavaLangBoolean:(JavaLangBoolean *)scrollEnabled {
  self->scrollEnabled_ = scrollEnabled;
}

- (void)setShowsVerticalScrollIndicatorWithJavaLangBoolean:(JavaLangBoolean *)showsVerticalScrollIndicator {
  self->showsVerticalScrollIndicator_ = showsVerticalScrollIndicator;
}

- (void)setShowRowDividerWithBoolean:(BOOL)showRowDivider {
  self->showRowDivider_ = showRowDivider;
}

- (void)setTableLayoutIdWithNSString:(NSString *)tableLayoutId {
  self->tableLayoutId_ = tableLayoutId;
}

- (void)setStickyHeadersWithBoolean:(BOOL)stickyHeaders {
  self->stickyHeaders_ = stickyHeaders;
}

- (void)setPaddingBottomWithJavaLangDouble:(JavaLangDouble *)paddingBottom {
  self->paddingBottom_ = paddingBottom;
}

- (void)setScrollToBottomOnLoadWithBoolean:(BOOL)scrollToBottomOnLoad {
  self->scrollToBottomOnLoad_ = scrollToBottomOnLoad;
}

- (void)setScrollToTopWhenUpdateWithBoolean:(BOOL)scrollToTopWhenUpdate {
  self->scrollToTopWhenUpdate_ = scrollToTopWhenUpdate;
}

- (void)copyAllFieldsTo:(FFTViewBehaviorTable *)other {
  [super copyAllFieldsTo:other];
  other->footerHeight_ = footerHeight_;
  other->headerHeight_ = headerHeight_;
  other->paddingBottom_ = paddingBottom_;
  other->rowHeight_ = rowHeight_;
  other->rowProvider_ = rowProvider_;
  other->scrollEnabled_ = scrollEnabled_;
  other->scrollToBottomOnLoad_ = scrollToBottomOnLoad_;
  other->scrollToTopWhenUpdate_ = scrollToTopWhenUpdate_;
  other->sectionFooterHeight_ = sectionFooterHeight_;
  other->sectionHeaderHeight_ = sectionHeaderHeight_;
  other->showRowDivider_ = showRowDivider_;
  other->showsVerticalScrollIndicator_ = showsVerticalScrollIndicator_;
  other->stickyHeaders_ = stickyHeaders_;
  other->tableLayoutId_ = tableLayoutId_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "initWithFFTKeyValueList:withFFTViewBehaviorTable_RowProvider:", "ViewBehaviorTable", NULL, 0x1, NULL },
    { "setRowProviderWithFFTViewBehaviorTable_RowProvider:", "setRowProvider", "V", 0x1, NULL },
    { "getRowOrSectionAtWithInt:", "getRowOrSectionAt", "Ljava.lang.Object;", 0x1, NULL },
    { "getItemIdWithInt:", "getItemId", "J", 0x1, NULL },
    { "getCount", NULL, "I", 0x1, NULL },
    { "getHeightFromObjectWithWithLong:", "getHeightFromObjectWith", "D", 0x1, NULL },
    { "description", "toString", "Ljava.lang.String;", 0x1, NULL },
    { "getRowHeight", NULL, "Ljava.lang.Double;", 0x1, NULL },
    { "getSectionFooterHeight", NULL, "Ljava.lang.Double;", 0x1, NULL },
    { "getSectionHeaderHeight", NULL, "Ljava.lang.Double;", 0x1, NULL },
    { "getHeaderHeight", NULL, "Ljava.lang.Double;", 0x1, NULL },
    { "getFooterHeight", NULL, "Ljava.lang.Double;", 0x1, NULL },
    { "getScrollEnabled", NULL, "Ljava.lang.Boolean;", 0x1, NULL },
    { "getShowsVerticalScrollIndicator", NULL, "Ljava.lang.Boolean;", 0x1, NULL },
    { "isShowRowDivider", NULL, "Z", 0x1, NULL },
    { "getRowProvider", NULL, "Lcom.sponberg.fluid.layout.ViewBehaviorTable$RowProvider;", 0x1, NULL },
    { "getTableLayoutId", NULL, "Ljava.lang.String;", 0x1, NULL },
    { "isStickyHeaders", NULL, "Z", 0x1, NULL },
    { "getPaddingBottom", NULL, "Ljava.lang.Double;", 0x1, NULL },
    { "isScrollToBottomOnLoad", NULL, "Z", 0x1, NULL },
    { "isScrollToTopWhenUpdate", NULL, "Z", 0x1, NULL },
    { "setRowHeightWithJavaLangDouble:", "setRowHeight", "V", 0x1, NULL },
    { "setSectionFooterHeightWithJavaLangDouble:", "setSectionFooterHeight", "V", 0x1, NULL },
    { "setSectionHeaderHeightWithJavaLangDouble:", "setSectionHeaderHeight", "V", 0x1, NULL },
    { "setHeaderHeightWithJavaLangDouble:", "setHeaderHeight", "V", 0x1, NULL },
    { "setFooterHeightWithJavaLangDouble:", "setFooterHeight", "V", 0x1, NULL },
    { "setScrollEnabledWithJavaLangBoolean:", "setScrollEnabled", "V", 0x1, NULL },
    { "setShowsVerticalScrollIndicatorWithJavaLangBoolean:", "setShowsVerticalScrollIndicator", "V", 0x1, NULL },
    { "setShowRowDividerWithBoolean:", "setShowRowDivider", "V", 0x1, NULL },
    { "setTableLayoutIdWithNSString:", "setTableLayoutId", "V", 0x1, NULL },
    { "setStickyHeadersWithBoolean:", "setStickyHeaders", "V", 0x1, NULL },
    { "setPaddingBottomWithJavaLangDouble:", "setPaddingBottom", "V", 0x1, NULL },
    { "setScrollToBottomOnLoadWithBoolean:", "setScrollToBottomOnLoad", "V", 0x1, NULL },
    { "setScrollToTopWhenUpdateWithBoolean:", "setScrollToTopWhenUpdate", "V", 0x1, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "rowHeight_", NULL, 0x2, "Ljava.lang.Double;", NULL,  },
    { "sectionFooterHeight_", NULL, 0x2, "Ljava.lang.Double;", NULL,  },
    { "sectionHeaderHeight_", NULL, 0x2, "Ljava.lang.Double;", NULL,  },
    { "headerHeight_", NULL, 0x2, "Ljava.lang.Double;", NULL,  },
    { "footerHeight_", NULL, 0x2, "Ljava.lang.Double;", NULL,  },
    { "scrollEnabled_", NULL, 0x2, "Ljava.lang.Boolean;", NULL,  },
    { "showsVerticalScrollIndicator_", NULL, 0x2, "Ljava.lang.Boolean;", NULL,  },
    { "showRowDivider_", NULL, 0x2, "Z", NULL,  },
    { "rowProvider_", NULL, 0x2, "Lcom.sponberg.fluid.layout.ViewBehaviorTable$RowProvider;", NULL,  },
    { "tableLayoutId_", NULL, 0x2, "Ljava.lang.String;", NULL,  },
    { "stickyHeaders_", NULL, 0x2, "Z", NULL,  },
    { "paddingBottom_", NULL, 0x2, "Ljava.lang.Double;", NULL,  },
    { "scrollToBottomOnLoad_", NULL, 0x2, "Z", NULL,  },
    { "scrollToTopWhenUpdate_", NULL, 0x2, "Z", NULL,  },
  };
  static J2ObjcClassInfo _FFTViewBehaviorTable = { "ViewBehaviorTable", "com.sponberg.fluid.layout", NULL, 0x1, 34, methods, 14, fields, 0, NULL};
  return &_FFTViewBehaviorTable;
}

@end

@interface FFTViewBehaviorTable_RowProvider : NSObject
@end

@implementation FFTViewBehaviorTable_RowProvider

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "getRowLayout", NULL, "Ljava.lang.String;", 0x401, NULL },
    { "getRowOrSectionAtWithInt:", "getRowOrSectionAt", "Ljava.lang.Object;", 0x401, NULL },
    { "getRowInSectionAtWithInt:withInt:", "getRowInSectionAt", "Lcom.sponberg.fluid.layout.TableRow;", 0x401, NULL },
    { "getItemIdAtWithInt:", "getItemIdAt", "J", 0x401, NULL },
    { "getItemIdAtWithInt:withInt:", "getItemIdAt", "J", 0x401, NULL },
    { "getCount", NULL, "I", 0x401, NULL },
    { "getHeightFromObjectWithWithLong:", "getHeightFromObjectWith", "D", 0x401, NULL },
    { "getSectionAtWithInt:", "getSectionAt", "Lcom.sponberg.fluid.layout.TableLayout$TableSection;", 0x401, NULL },
    { "getNumSections", NULL, "I", 0x401, NULL },
    { "getNumRowsInSectionWithInt:", "getNumRowsInSection", "I", 0x401, NULL },
    { "getRowIndexOfObjectWithLong:", "getRowIndexOfObject", "I", 0x401, NULL },
    { "getIndexOfRecentlyDeletedObjectWithLong:", "getIndexOfRecentlyDeletedObject", "I", 0x401, NULL },
  };
  static J2ObjcClassInfo _FFTViewBehaviorTable_RowProvider = { "RowProvider", "com.sponberg.fluid.layout", "ViewBehaviorTable", 0x201, 12, methods, 0, NULL, 0, NULL};
  return &_FFTViewBehaviorTable_RowProvider;
}

@end
