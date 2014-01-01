package org.jphototagger.tcc.def;

import java.util.ArrayList;
import java.util.Collection;
import javax.swing.Action;
import org.jphototagger.domain.filetypes.UserDefinedFileType;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
public final class SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction extends SaveFileSuffixesAsUserDefinedFileTypesAction {

    private static final long serialVersionUID = 1L;
    private static final Collection<UserDefinedFileType> FILE_TYPES = new ArrayList<>();
    public static final SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction INSTANCE = new SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction();

    {
        // Videos
        FILE_TYPES.add(createUserDefinedFileType("avi", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.avi.Description")));
        FILE_TYPES.add(createUserDefinedFileType("flv", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.flv.Description")));
        FILE_TYPES.add(createUserDefinedFileType("m2ts", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.m2ts.Description")));
        FILE_TYPES.add(createUserDefinedFileType("mkv", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.mkv.Description")));
        FILE_TYPES.add(createUserDefinedFileType("mov", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.mov.Description")));
        FILE_TYPES.add(createUserDefinedFileType("mp2", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.mp2.Description")));
        FILE_TYPES.add(createUserDefinedFileType("mp4", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.mp4.Description")));
        FILE_TYPES.add(createUserDefinedFileType("mpeg", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.mpeg.Description")));
        FILE_TYPES.add(createUserDefinedFileType("mpg", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.mpg.Description")));
        FILE_TYPES.add(createUserDefinedFileType("mts", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.mts.Description")));
        FILE_TYPES.add(createUserDefinedFileType("ts", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.ts.Description")));
        FILE_TYPES.add(createUserDefinedFileType("wmv", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.wmv.Description")));

        // Images not handled by JPhotoTagger
        FILE_TYPES.add(createUserDefinedFileType("bmp", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.bmp.Description")));
        FILE_TYPES.add(createUserDefinedFileType("ttf", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.ttf.Description")));
        FILE_TYPES.add(createUserDefinedFileType("xcf", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.xcf.Description")));
    }

    private SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction() {
        super(FILE_TYPES);
        putValue(Action.NAME, Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.Name"));
    }
                }
