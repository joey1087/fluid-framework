//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/fluid/parser/ViewsParser.java
//

#ifndef _FFTViewsParser_H_
#define _FFTViewsParser_H_

@class FFTCoord;
@class FFTFluidApp;
@class FFTKVLReader;
@class FFTLayout;
@class FFTLayout_AlignEnum;
@class FFTLayout_DirectionEnum;
@class FFTLength;
@class FFTTableLayout;
@class FFTView;
@class IOSObjectArray;
@class JavaUtilArrayList;
@class JavaUtilHashMap;
@class JavaUtilHashSet;
@protocol JavaUtilList;
@protocol JavaUtilSet;

#import "JreEmulation.h"
#include "com/sponberg/fluid/ApplicationInitializer.h"
#include "com/sponberg/fluid/util/KeyValueList.h"

@interface FFTViewsParser : NSObject < FFTApplicationInitializer > {
 @public
  FFTFluidApp *app_;
  FFTKVLReader *settings_;
  id<FFTKeyValueList> screen_;
  JavaUtilHashMap *viewById_;
  FFTLayout *currentLayout_;
  BOOL anchorSet_;
  FFTLayout_DirectionEnum *nextHorDir_;
  FFTLayout_AlignEnum *nextAlignment_;
  NSString *currentFile_;
  NSString *lastLine_;
  JavaUtilHashMap *viewObjectById_;
  BOOL parsingLandscape_;
}

- (void)initialize__WithFFTFluidApp:(FFTFluidApp *)app OBJC_METHOD_FAMILY_NONE;

- (NSString *)getPlatformOrDefaultResourceWithNSString:(NSString *)dir
                                          withNSString:(NSString *)defaultName;

- (id<FFTKeyValueList>)getScreenKVLWithNSString:(NSString *)screenAsString;

- (FFTLayout *)parseLayoutWithNSString:(NSString *)id_
                   withFFTKeyValueList:(id<FFTKeyValueList>)screen
                            withDouble:(double)baseUnit;

- (FFTLayout *)parseLayoutWithNSString:(NSString *)layoutIdPrefix
                          withNSString:(NSString *)id_
                   withFFTKeyValueList:(id<FFTKeyValueList>)reader
                            withDouble:(double)baseUnit;

- (void)parseLayoutWithJavaUtilList:(id<JavaUtilList>)lines
                   withJavaUtilList:(id<JavaUtilList>)layoutVariables;

- (JavaUtilHashMap *)parseRowPropertiesWithNSString:(NSString *)lineProperties
                                withJavaUtilHashMap:(JavaUtilHashMap *)properties;

- (void)fixRelativeLengthsWithDouble:(double)baseUnit;

- (void)setupViewById;

- (void)parseRowWithNSString:(NSString *)s
         withJavaUtilHashMap:(JavaUtilHashMap *)rowProperties
            withJavaUtilList:(id<JavaUtilList>)layoutVariables;

- (void)parseDownWithNSString:(NSString *)s;

- (void)parseNewLayerWithJavaUtilHashMap:(JavaUtilHashMap *)rowProperties;

- (FFTView *)createViewWithNSString:(NSString *)id_
                withJavaUtilHashMap:(JavaUtilHashMap *)rowProperties
                   withJavaUtilList:(id<JavaUtilList>)layoutVariables;

- (FFTLength *)getLengthWithNSString:(NSString *)value;

- (FFTCoord *)getCoordWithNSString:(NSString *)value;

- (FFTLength *)parseLengthWithNSStringArray:(IOSObjectArray *)sa
                               withNSString:(NSString *)value;

- (FFTLength *)parseLengthWithNSStringArray:(IOSObjectArray *)sa
                               withNSString:(NSString *)value
                                    withInt:(int)index;

- (void)parseConstraintsWithNSString:(NSString *)value
               withJavaUtilArrayList:(JavaUtilArrayList *)subtractors
                   withNSStringArray:(IOSObjectArray *)sa
                             withInt:(int)index;

+ (NSString *)getTableLayoutIdWithNSString:(NSString *)tableLayoutId
                              withNSString:(NSString *)layoutId;

- (FFTTableLayout *)parseTableLayoutWithNSString:(NSString *)id_
                                    withNSString:(NSString *)layoutAsString
                                      withDouble:(double)baseUnit;

- (IOSObjectArray *)getSupportedPlatforms;

- (id)init;

- (void)copyAllFieldsTo:(FFTViewsParser *)other;

@end

__attribute__((always_inline)) inline void FFTViewsParser_init() {}

J2OBJC_FIELD_SETTER(FFTViewsParser, app_, FFTFluidApp *)
J2OBJC_FIELD_SETTER(FFTViewsParser, settings_, FFTKVLReader *)
J2OBJC_FIELD_SETTER(FFTViewsParser, screen_, id<FFTKeyValueList>)
J2OBJC_FIELD_SETTER(FFTViewsParser, viewById_, JavaUtilHashMap *)
J2OBJC_FIELD_SETTER(FFTViewsParser, currentLayout_, FFTLayout *)
J2OBJC_FIELD_SETTER(FFTViewsParser, nextHorDir_, FFTLayout_DirectionEnum *)
J2OBJC_FIELD_SETTER(FFTViewsParser, nextAlignment_, FFTLayout_AlignEnum *)
J2OBJC_FIELD_SETTER(FFTViewsParser, currentFile_, NSString *)
J2OBJC_FIELD_SETTER(FFTViewsParser, lastLine_, NSString *)
J2OBJC_FIELD_SETTER(FFTViewsParser, viewObjectById_, JavaUtilHashMap *)

typedef FFTViewsParser ComSponbergFluidParserViewsParser;

@interface FFTViewsParser_KeyValueListWithRowProperties : NSObject < FFTKeyValueList > {
 @public
  id<FFTKeyValueList> list_;
  JavaUtilHashMap *rowProperties_;
  id<JavaUtilList> layoutVariables_;
  JavaUtilHashSet *keys__;
}

- (id)initWithFFTKeyValueList:(id<FFTKeyValueList>)list
          withJavaUtilHashMap:(JavaUtilHashMap *)rowProperties
             withJavaUtilList:(id<JavaUtilList>)layoutVariables;

- (id<JavaUtilList>)getWithNSString:(NSString *)key;

- (id<FFTKeyValueList>)getWithValueWithNSString:(NSString *)key
                                   withNSString:(NSString *)value;

- (BOOL)containsWithNSString:(NSString *)key;

- (id<JavaUtilList>)getValuesWithNSString:(NSString *)key;

- (NSString *)getValueWithNSString:(NSString *)key;

- (NSString *)getSizeValueWithNSString:(NSString *)key;

- (NSString *)getValue;

- (id<JavaUtilSet>)keys;

- (void)copyAllFieldsTo:(FFTViewsParser_KeyValueListWithRowProperties *)other;

@end

__attribute__((always_inline)) inline void FFTViewsParser_KeyValueListWithRowProperties_init() {}

J2OBJC_FIELD_SETTER(FFTViewsParser_KeyValueListWithRowProperties, list_, id<FFTKeyValueList>)
J2OBJC_FIELD_SETTER(FFTViewsParser_KeyValueListWithRowProperties, rowProperties_, JavaUtilHashMap *)
J2OBJC_FIELD_SETTER(FFTViewsParser_KeyValueListWithRowProperties, layoutVariables_, id<JavaUtilList>)
J2OBJC_FIELD_SETTER(FFTViewsParser_KeyValueListWithRowProperties, keys__, JavaUtilHashSet *)

#endif // _FFTViewsParser_H_
