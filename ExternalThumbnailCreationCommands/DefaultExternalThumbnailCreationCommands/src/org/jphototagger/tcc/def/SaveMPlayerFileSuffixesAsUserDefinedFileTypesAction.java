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
    public static final SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction INSTANCE = new SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction();

    private static Collection<UserDefinedFileType> createFileTypes() {
        Collection<UserDefinedFileType> fileTypes = new ArrayList<>();

        // Videos
        fileTypes.add(createUserDefinedFileType("avi", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.avi.Description")));
        fileTypes.add(createUserDefinedFileType("flv", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.flv.Description")));
        fileTypes.add(createUserDefinedFileType("m2ts", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.m2ts.Description")));
        fileTypes.add(createUserDefinedFileType("mkv", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.mkv.Description")));
        fileTypes.add(createUserDefinedFileType("mov", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.mov.Description")));
        fileTypes.add(createUserDefinedFileType("mp2", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.mp2.Description")));
        fileTypes.add(createUserDefinedFileType("mp4", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.mp4.Description")));
        fileTypes.add(createUserDefinedFileType("mpeg", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.mpeg.Description")));
        fileTypes.add(createUserDefinedFileType("mpg", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.mpg.Description")));
        fileTypes.add(createUserDefinedFileType("mts", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.mts.Description")));
        fileTypes.add(createUserDefinedFileType("ts", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.ts.Description")));
        fileTypes.add(createUserDefinedFileType("wmv", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.wmv.Description")));

        // Images not handled by JPhotoTagger
        fileTypes.add(createUserDefinedFileType("bmp", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.bmp.Description")));
        fileTypes.add(createUserDefinedFileType("ttf", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.ttf.Description")));
        fileTypes.add(createUserDefinedFileType("xcf", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.xcf.Description")));

        return fileTypes;
    }

    private SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction() {
        super(createFileTypes());
        putValue(Action.NAME, Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.Name"));
    }
}
