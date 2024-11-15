/*
 * Copyright (c) 2011, 2012, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

#import <Cocoa/Cocoa.h>

@interface CFileDialog : NSObject <NSOpenSavePanelDelegate> {
    NSWindow* fOwner;

    // Should we query back to Java for a file filter?
    jboolean fHasFileFilter;

    // Allowed file types
    NSArray *fFileTypes;

    // sun.awt.CFileDialog
    jobject fFileDialog;

    // Return value from dialog
    NSInteger fPanelResult;

    // Dialog's title
    NSString *fTitle;

    // Starting directory and file
    NSString *fDirectory;
    NSString *fFile;

    // File dialog's mode
    jint fMode;

    // File dialog's modality type
    jint fModality;

    // Indicates whether the user can select multiple files
    BOOL fMultipleMode;

    // Should we navigate into apps?
    BOOL fNavigateApps;

    // Can the dialog choose directories ?
    BOOL fChooseDirectories;

    // Can the dialog choose files ?
    BOOL fChooseFiles;

    // Can the dialog create directories ?
    BOOL fCreateDirectories;

    // Contains the absolute paths of the selected files as URLs
    NSArray *fURLs;

    // Condition to signal when dialog is closed
    NSCondition *fCondition;
}

// Allocator
- (id) initWithOwner:(NSWindow*) owner
               filter:(jboolean)inHasFilter
            fileTypes:(NSArray *)inFileTypes
           fileDialog:(jobject)inDialog
                title:(NSString *)inTitle
            directory:(NSString *)inPath
                 file:(NSString *)inFile
                 mode:(jint)inMode
             modality:(jint)inModality
         multipleMode:(BOOL)inMultipleMode
       shouldNavigate:(BOOL)inNavigateApps
 canChooseDirectories:(BOOL)inChooseDirectories
       canChooseFiles:(BOOL)inChooseFiles
 canCreateDirectories:(BOOL)inCreateDirectories
              withEnv:(JNIEnv*)env;

// Invoked from the main thread
- (void) safeSaveOrLoad;

// Wait for completion and get dialog return value
- (NSModalResponse) wait;

// Returns the absolute paths of the selected files as URLs
- (NSArray *) URLs;

@end
