//
//  FFDatastoreService.m
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 22/04/2014.
//  Copyright (c) 2014 FluidFramework.org. All rights reserved.
//

#import <UIKit/UIKit.h>

#include "java/util/ArrayList.h"
#include "java/lang/Integer.h"
#include "java/lang/Double.h"
#include "java/lang/Long.h"
#include "IOSClass.h"

#import "FFDatastoreService.h"
#import "DatastoreService.h"
#import "DatastoreException.h"
#import "SQLInsert.h"
#import "SQLUpdate.h"
#import "SQLQuery.h"
#import "SQLQueryResult.h"
#import "SQLParameterizedStatement.h"
#import "SQLQueryJoin.h"
#import "SQLQueryJoin3.h"
#import "SQLQueryJoin4.h"
#import "SQLExecutableQuery.h"
#import "SQLResultList.h"

#include <sys/xattr.h>

@interface FFDatastoreService ()

@property (nonatomic, strong) NSMutableDictionary *hasSetDoNotBackupForDatabase;

@end

@implementation FFDatastoreService


+ (NSString *)databasePath:(NSString *)databaseName {
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *writableDBPath = [documentsDirectory stringByAppendingPathComponent:databaseName];
    return writableDBPath;
}

- (BOOL)doesDatabaseExistWithNSString:(NSString *)databaseName {
    
    NSFileManager *fileManager = [NSFileManager defaultManager];
    return [fileManager fileExistsAtPath:[FFDatastoreService databasePath:databaseName]];
}

- (BOOL)deployDatabaseFromBundleWithNSString:(NSString *)databaseName {

    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSString *defaultDBPath = [[[NSBundle mainBundle] resourcePath] stringByAppendingPathComponent:databaseName];
    
    if (![fileManager fileExistsAtPath:defaultDBPath]) {
        return NO;
    }
    
    return [fileManager copyItemAtPath:defaultDBPath toPath:[FFDatastoreService databasePath:databaseName] error:nil];
}

- (void)openDatabaseWithNSString:(NSString *)databaseName {

    NSString *databasePath = [FFDatastoreService databasePath:databaseName];
    
    if (sqlite3_open([databasePath UTF8String], &_database) != SQLITE_OK) {
        @throw [[FFTDatastoreException alloc] initWithNSString:[NSString stringWithFormat:@"Failed to open database: %@", databasePath]];
    }
    
    if (!self.hasSetDoNotBackupForDatabase) {
        self.hasSetDoNotBackupForDatabase = [NSMutableDictionary dictionary];
    }
    
    if (![self.hasSetDoNotBackupForDatabase objectForKey:databaseName]) {
        [self.hasSetDoNotBackupForDatabase setObject:[NSNumber numberWithBool:NO] forKey:databaseName];
    }
    
    BOOL hasAddedSkip = [[self.hasSetDoNotBackupForDatabase objectForKey:databaseName] boolValue];
    if (!hasAddedSkip) {
        [self addSkipBackupAttributeToItemAtURL:[NSURL URLWithString:[FFDatastoreService databasePath:databaseName]]];
        [self.hasSetDoNotBackupForDatabase setObject:[NSNumber numberWithBool:YES] forKey:databaseName];
    }
}

- (void)addSkipBackupAttributeToItemAtURL:(NSURL *)fileURL {
    // http://iosameer.blogspot.com.au/2013/02/nsdocumentsdirectory-and-icloud-issue.html
    // First ensure the file actually exists
    if (![[NSFileManager defaultManager] fileExistsAtPath:[fileURL path]]) {
        NSLog(@"File %@ doesn't exist!",[fileURL path]);
        return;
    }
    // Determine the iOS version to choose correct skipBackup method
    NSString *currSysVer = [[UIDevice currentDevice] systemVersion];
    if ([currSysVer isEqualToString:@"5.0.1"]) {
        const char* filePath = [[fileURL path] fileSystemRepresentation];
        const char* attrName = "com.apple.MobileBackup";
        u_int8_t attrValue = 1;
        setxattr(filePath, attrName, &attrValue, sizeof(attrValue), 0, 0);
        NSLog(@"Excluded '%@' from backup",fileURL);
        return;
    }
    else if (&NSURLIsExcludedFromBackupKey) { //iOS 5.1 and later
        NSError *error = nil;
        BOOL result = [fileURL setResourceValue:[NSNumber numberWithBool:YES] forKey:NSURLIsExcludedFromBackupKey error:&error];
        if (result == NO) {
            NSLog(@"Error excluding '%@' from backup. Error: %@",fileURL, error);
            return;
        }
        else { // Succeeded
            NSLog(@"Excluded '%@' from backup",fileURL);
            return;
        }
    } else {
        // iOS version is below 5.0, no need to do anything because there will be no iCloud present
        return;
    }
}

- (void)executeRawStatementWithNSString:(NSString *)statement {
    char *errMsg = NULL;
    if (sqlite3_exec(_database, [statement UTF8String], NULL, NULL, &errMsg) != SQLITE_OK) {
        @throw [[FFTDatastoreException alloc] initWithNSString:[NSString stringWithFormat:@"Failed to execute statement: %@", statement]];
    }
}

- (void)bindParameters:(FFTSQLParameterizedStatement *)pStatement to:(sqlite3_stmt *)statement {
    int index = 1;
    for (FFTSQLParameterizedStatement_Pair *pair in [pStatement getUpdateParamsInOrder]) {
        id param = [pair getValue];
        if (param == nil) {
            sqlite3_bind_null(statement, index);
        } else if ([param isKindOfClass:[JavaLangInteger class]]) {
            sqlite3_bind_int(statement, index, [param intValue]);
        } else if ([param isKindOfClass:[JavaLangDouble class]]) {
            sqlite3_bind_double(statement, index, [param doubleValue]);
        } else if ([param isKindOfClass:[JavaLangLong class]]) {
            sqlite3_bind_int(statement, index, [param longValue]);
        } else if ([param isKindOfClass:[NSString class]]) {
            sqlite3_bind_text(statement, index, [param UTF8String], -1, SQLITE_TRANSIENT);
        } else if ([param isKindOfClass:[IOSByteArray class]]) {
            NSData *bytes = [param toNSData];
            sqlite3_bind_blob(statement, index, [bytes bytes], [bytes length], SQLITE_TRANSIENT);
        } else {
            [NSException raise:@"Invalid sql parameter" format:@"Don't know how to bind %d %@ %@", index, [pair getKey], [pair getValue]];
        }
        index++;
    }
    for (id param in [pStatement getWhereParamsInOrder]) {
        if (param == nil) {
            sqlite3_bind_null(statement, index);
        } else if ([param isKindOfClass:[JavaLangInteger class]]) {
            sqlite3_bind_int(statement, index, [param intValue]);
        } else if ([param isKindOfClass:[JavaLangDouble class]]) {
            sqlite3_bind_int(statement, index, [param doubleValue]);
        } else if ([param isKindOfClass:[NSString class]]) {
            sqlite3_bind_text(statement, index, [param UTF8String], -1, SQLITE_TRANSIENT);
        } else if ([param isKindOfClass:[IOSByteArray class]]) {
            NSData *bytes = [param toNSData];
            sqlite3_bind_blob(statement, index, [bytes bytes], [bytes length], SQLITE_TRANSIENT);
        }
        index++;
    }
}

- (FFTSQLResultList *)queryWithFFTSQLQuery:(FFTSQLQuery *)query {
    [self executeQuery:query];
    return [query getResults];
}

- (FFTSQLResultList *)queryWithFFTSQLQueryJoin:(FFTSQLQueryJoin *)query {
    [self executeQuery:query];
    return [query getResults];
}

- (FFTSQLResultList *)queryWithFFTSQLQueryJoin3:(FFTSQLQueryJoin3 *)query {
    [self executeQuery:query];
    return [query getResults];
}

- (FFTSQLResultList *)queryWithFFTSQLQueryJoin4:(FFTSQLQueryJoin4 *)query {
    [self executeQuery:query];
    return [query getResults];
}

- (void)executeQuery:(id<FFTSQLExecutableQuery>)query {

    sqlite3_stmt *statement;
    
    BOOL success = YES;
    
    FFTSQLParameterizedStatement *pStatement = [query getParameterizedStatement];
    
    if (sqlite3_prepare_v2(_database, [[pStatement getUnboundSql] UTF8String], -1, &statement, NULL) == SQLITE_OK) {
        
        [self bindParameters:pStatement to:statement];
        
        while (sqlite3_step(statement) == SQLITE_ROW) {

            [query addResult];
            
            for (int i = 0; i < sqlite3_column_count(statement); i++) {
                int colType = sqlite3_column_type(statement, i);
                NSString *columnName = [NSString stringWithUTF8String:sqlite3_column_name(statement, i)];
                id value;
                if (colType == SQLITE_TEXT) {
                    const unsigned char *col = sqlite3_column_text(statement, i);
                    value = [NSString stringWithFormat:@"%s", col];
                    [query setStringWithInt:i withNSString:columnName withNSString:value];
                } else if (colType == SQLITE_INTEGER) {
                    int col = sqlite3_column_int(statement, i);
                    value = [JavaLangInteger valueOfWithInt:col];
                    [query setIntegerWithInt:i withNSString:columnName withJavaLangInteger:value];
                } else if (colType == SQLITE_FLOAT) {
                    double col = sqlite3_column_double(statement, i);
                    value = [JavaLangDouble valueOfWithDouble:col];
                    [query setDoubleWithInt:i withNSString:columnName withJavaLangDouble:value];
                } else if (colType == SQLITE_NULL) {
                    value = [NSNull null];
                    [query setNullWithInt:i withNSString:columnName];
                } else if (colType == SQLITE_BLOB) {
                    value = [IOSByteArray arrayWithBytes:sqlite3_column_blob(statement, i) count:sqlite3_column_bytes(statement, i)];
                    [query setBinaryWithInt:i withNSString:columnName withByteArray:value];
                } else {
                    NSLog(@"[SQLITE] UNKNOWN DATATYPE");
                }
                
            }
        }
    } else {
        success = NO;
    }
    sqlite3_finalize(statement); // hstdbc if we can reuse this to increase performance

    if (!success) {
        @throw [[FFTDatastoreException alloc] initWithNSString:[NSString stringWithFormat:@"Failed to execute query: %@", [pStatement getUnboundSql]]];
    }
}


- (void)updateWithFFTSQLUpdate:(FFTSQLUpdate *)update {
    
    sqlite3_stmt *statement;
    
    BOOL success = YES;
    
    FFTSQLParameterizedStatement *pStatement = [update getParameterizedStatement];
    
    if (sqlite3_prepare_v2(_database, [[pStatement getUnboundSql] UTF8String], -1, &statement, NULL) == SQLITE_OK) {
        
        [self bindParameters:pStatement to:statement];
        
        if (sqlite3_step(statement) != SQLITE_DONE) {
            success = NO;
        }
        
    } else {
        success = NO;
    }
    
    sqlite3_finalize(statement); // hstdbc if we can reuse this to increase performance

    if (!success) {
        @throw [[FFTDatastoreException alloc] initWithNSString:[NSString stringWithFormat:@"Failed to execute update: %@", [pStatement getUnboundSql]]];
    }

}

- (JavaLangLong *)insertWithFFTSQLInsert:(FFTSQLInsert *)insert {
    
    sqlite3_stmt *statement;
    
    BOOL success = YES;
    
    FFTSQLParameterizedStatement *pStatement = [insert getParameterizedStatement];
    
    if (sqlite3_prepare_v2(_database, [[pStatement getUnboundSql] UTF8String], -1, &statement, NULL) == SQLITE_OK) {

        [self bindParameters:pStatement to:statement];

        if (sqlite3_step(statement) != SQLITE_DONE) {
            success = NO;
        }

    } else {
        success = NO;
    }
    
    sqlite3_finalize(statement); // hstdbc if we can reuse this to increase performance
    
    long long int lastId = sqlite3_last_insert_rowid(_database);
    
    if (!success) {
        NSString *errorMessage = [NSString stringWithUTF8String:sqlite3_errmsg(_database)];
        @throw [[FFTDatastoreException alloc] initWithNSString:[NSString stringWithFormat:@"Failed to execute insert: %@ : %@", errorMessage, [pStatement getUnboundSql]]];
        return [JavaLangLong valueOfWithLong:-1];
    }
    
    return [JavaLangLong valueOfWithLong:lastId];
}

- (void)startTransaction {
    sqlite3_exec(_database, "BEGIN TRANSACTION EXCLUSIVE", 0, 0, 0);
}

- (void)commitTransaction {
    if (sqlite3_exec(_database, "COMMIT TRANSACTION", 0, 0, 0) != SQLITE_OK) {
        @throw [[FFTDatastoreException alloc] initWithNSString:[NSString stringWithFormat:@"Commit transaction failed"]];
    }
}

- (void)rollbackTransaction {
    sqlite3_exec(_database, "ROLLBACK TRANSACTION", 0, 0, 0);
}

- (void)closeDatabase {
    sqlite3_close(_database);
}

- (BOOL)doesBackupExistWithNSString:(NSString *)databaseName {
    NSString *backupName = [databaseName stringByAppendingString:FFTDatastoreService_backupSuffix_];
    return [self doesDatabaseExistWithNSString:backupName];
}

- (void)restoreBackupWithNSString:(NSString *)databaseName {
    
    NSString *backupName = [databaseName stringByAppendingString:FFTDatastoreService_backupSuffix_];
    NSFileManager *manager = [NSFileManager defaultManager];
    
    if ([self doesBackupExistWithNSString:databaseName])
        [manager removeItemAtPath:[FFDatastoreService databasePath:databaseName] error:nil];
    
    BOOL success = [manager copyItemAtPath:[FFDatastoreService databasePath:backupName]
                     toPath:[FFDatastoreService databasePath:databaseName] error:nil];
    if (!success) {
        @throw [[FFTDatastoreException alloc] initWithNSString:[NSString stringWithFormat:@"Unable to restore backup"]];
    }
}

- (void)backupDatabaseWithNSString:(NSString *)databaseName {

    NSString *backupName = [databaseName stringByAppendingString:FFTDatastoreService_backupSuffix_];
    NSFileManager *manager = [NSFileManager defaultManager];

    if ([self doesBackupExistWithNSString:databaseName])
        [manager removeItemAtPath:[FFDatastoreService databasePath:backupName] error:nil];
    
    BOOL success = [manager copyItemAtPath:[FFDatastoreService databasePath:databaseName]
                                    toPath:[FFDatastoreService databasePath:backupName] error:nil];
    if (!success) {
        @throw [[FFTDatastoreException alloc] initWithNSString:[NSString stringWithFormat:@"Unable to backup database"]];
    }
}

- (void)deleteBackupWithNSString:(NSString *)databaseName {

    NSString *backupName = [databaseName stringByAppendingString:FFTDatastoreService_backupSuffix_];

    NSFileManager *manager = [NSFileManager defaultManager];
    BOOL success = [manager removeItemAtPath:[FFDatastoreService databasePath:backupName] error:nil];
    if (!success) {
        @throw [[FFTDatastoreException alloc] initWithNSString:[NSString stringWithFormat:@"Unable to delete backup"]];
    }
}

- (void)deleteDatabaseWithNSString:(NSString *)databaseName {
 
    NSFileManager *fileManager = [NSFileManager defaultManager];
    if ([fileManager fileExistsAtPath:[FFDatastoreService databasePath:databaseName]]) {
        BOOL success = [fileManager removeItemAtPath:[FFDatastoreService databasePath:databaseName] error:nil];
        if (!success) {
            @throw [[FFTDatastoreException alloc] initWithNSString:[NSString stringWithFormat:@"Unable to delete database"]];
        }
    }

    // and journal file
    NSString *journalName = [databaseName stringByAppendingString:@"-journal"];
    if ([fileManager fileExistsAtPath:[FFDatastoreService databasePath:journalName]]) {
        BOOL success = [fileManager removeItemAtPath:[FFDatastoreService databasePath:journalName] error:nil];
        if (!success) {
            @throw [[FFTDatastoreException alloc] initWithNSString:[NSString stringWithFormat:@"Unable to delete database journal"]];
        }
    }
}

@end
