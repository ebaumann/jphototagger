[Setup]
AppName=JPhotoTagger
AppVerName=JPhotoTagger 0.30.4
AppVersion=0.30.4
AppPublisher=Elmar Baumann <eb@elmar-baumann.de>
AppPublisherURL=http://www.jphototagger.org/
AppSupportURL=mailto:support@jphototagger.org
AppUpdatesURL=http://www.jphototagger.org/download.html
AppComments=Photo Manager
DefaultDirName={pf}\JPhotoTagger
DefaultGroupName=JPhotoTagger
AllowNoIcons=yes
OutputDir={#SourcePath}\..\..\..\dist_files\upload
OutputBaseFilename=JPhotoTagger-setup
SetupIconFile={#SourcePath}\..\..\img\JPhotoTagger.ico
WizardImageFile={#SourcePath}\..\..\img\WizardImageFile.bmp
Compression=lzma
SolidCompression=yes
UninstallDisplayIcon={app}\unins000.exe
ArchitecturesInstallIn64BitMode=x64 ia64

[Languages]
Name: "de"; \
  MessagesFile: "compiler:Languages\German.isl"; \
  InfoAfterFile: "JPhotoTagger-Readme_de.txt"
Name: "en"; \
  MessagesFile: "compiler:Languages\Default.isl"; \
  InfoAfterFile: "JPhotoTagger-Readme_en.txt"

[Tasks]
Name: "desktopicon"; \
  Description: "{cm:CreateDesktopIcon}"; \
  GroupDescription: "{cm:AdditionalIcons}"; \
  Flags: unchecked

[Files]
Source: "{#SourcePath}\..\..\..\Program\dist\JPhotoTagger.jar"; \
  DestDir: "{app}"; \
  AfterInstall: CreateBatchFile; \
  Flags: ignoreversion
Source: "{#SourcePath}\..\..\..\Program\dist\lib\*"; \
  DestDir: "{app}\lib"; \
  Flags: ignoreversion
Source: "{#SourcePath}\..\..\img\JPhotoTagger.ico"; \
  DestDir: "{app}"; \
  Flags: ignoreversion
Source: "{#SourcePath}\..\..\..\dist_files\manual\Manual_de.pdf"; \
  DestDir: "{app}"; \
  Flags: ignoreversion
Source: "{#SourcePath}\..\..\..\dist_files\script\rotatejpg.sh"; \
  DestDir: "{app}\scripts"
Source: "{#SourcePath}\..\..\..\dist_files\script\thumbnail2stdoutwin.bat"; \
  DestDir: "{app}\scripts"
Source: "{#SourcePath}\..\..\..\dist_files\script\thumbnail2stdoutwin.sh"; \
  DestDir: "{app}\scripts"
Source: "{#SourcePath}\..\..\..\dist_files\dcraw\bin\win32\*"; \
  DestDir: "{app}\lib\dcraw\win32"
Source: "{#SourcePath}\..\..\..\dist_files\dcraw\bin\win64\*"; \
  DestDir: "{app}\lib\dcraw\win64"

[Icons]
Name: "{group}\JPhotoTagger"; \
  Filename: "{app}\JPhotoTagger.bat"; \
  IconFilename: "{app}\JPhotoTagger.ico"; \
  Flags: runminimized
Name: "{group}\Handbuch (PDF)"; \
  Filename: "{app}\Manual_de.pdf"
Name: "{group}\{cm:ProgramOnTheWeb,JPhotoTagger}"; \
  Filename: "http://www.jphototagger.org/"; \
  IconFilename: "{app}\JPhotoTagger.ico"
Name: "{commondesktop}\JPhotoTagger"; \
  Filename: "{app}\JPhotoTagger.bat"; \
  Tasks: desktopicon; \
  IconFilename: "{app}\JPhotoTagger.ico"; \
  Flags: runminimized

[Run]
Filename: "{app}\JPhotoTagger.bat"; \
  Description: "{cm:LaunchProgram,JPhotoTagger}"; \
  Flags: nowait postinstall skipifsilent runminimized

[Messages]
de.WelcomeLabel2=[name/ver] wird installiert.
de.FinishedLabel=Fertig. Bitte denken Sie daran, dass Java 7 (1.7) oder höher installiert sein muss!

en.WelcomeLabel2=[name/ver] will be installed.
en.FinishedLabel=Fertig. Please ensure, that Java 7 (1.7) or higher is installed!

[CustomMessages]
de.JvmUserLanguageCaption=Sprache
de.JvmUserLanguageDescription=JPhotoTaggers Sprache
de.JvmUserLanguageSubCaption=JPhotoTaggers Benutzeroberflächen-Sprache
de.JvmUserLanguageOptionAuto=Automatisch
de.JvmUserLanguageOptionDe=Deutsch
de.JvmUserLanguageOptionEn=Englisch
de.JvmXmxCaption=Maximaler Arbeitsspeicher
de.JvmXmxDescription=Maximaler Arbeitsspeicher für JPhotoTagger
de.JvmXmxSubCaption=Maximaler Arbeitsspeicher für JPhotoTagger
de.JvmXmx500Mb=500 Megabyte
de.JvmXmx750Mb=750 Megabyte
de.JvmXmx1Gb=1 Gigabyte

en.JvmUserLanguageCaption=Language
en.JvmUserLanguageDescription=JPhotoTagger's Language
en.JvmUserLanguageSubCaption=JPhotoTagger's GUI Language
en.JvmUserLanguageOptionAuto=Automatically
en.JvmUserLanguageOptionDe=German
en.JvmUserLanguageOptionEn=English
en.JvmXmxCaption=Maximum Memory
en.JvmXmxDescription=Maximum Memory for JPhotoTagger
en.JvmXmxSubCaption=Maximum Memory for JPhotoTagger
en.JvmXmx500Mb=500 Megabytes
en.JvmXmx750Mb=750 Megabytes
en.JvmXmx1Gb=1 Gigabyte

[Code]
const
  JPT_REGISTRY_KEY = 'Software\JPhotoTagger';
  USER_LANGUAGE_INDEX_REG_VALUE_NAME = 'InstallerUserLanguageIndex';
  MAXIMUM_MEMORY_INDEX_REG_VALUE_NAME = 'InstallerMaximumMemoryIndex';

var
  UserLanguage: String;
  UserLanguagePage: TInputOptionWizardPage;
  MaximumMemory: String;
  MaximumMemoryPage: TInputOptionWizardPage;

function GetUserLanguageIndex(): Cardinal;
var
  Index: Cardinal;
begin
  if (RegQueryDWordValue(HKEY_CURRENT_USER, JPT_REGISTRY_KEY, USER_LANGUAGE_INDEX_REG_VALUE_NAME, Index))
  then Result := Index
  else Result := 0;
end;

procedure StoreUserLanguageIndex(const Index: Cardinal);
begin
  RegWriteDWordValue(HKEY_CURRENT_USER, JPT_REGISTRY_KEY, USER_LANGUAGE_INDEX_REG_VALUE_NAME, Index);
end;

function GetMaximumMemoryIndex(): Cardinal;
var
  Index: Cardinal;
begin
  if (RegQueryDWordValue(HKEY_CURRENT_USER, JPT_REGISTRY_KEY, MAXIMUM_MEMORY_INDEX_REG_VALUE_NAME, Index))
  then Result := Index
  else Result := 1;
end;

procedure StoreMaximumMemoryIndex(const Index: Cardinal);
begin
  RegWriteDWordValue(HKEY_CURRENT_USER, JPT_REGISTRY_KEY, MAXIMUM_MEMORY_INDEX_REG_VALUE_NAME, Index);
end;

procedure CreateUserLanguagePage();
begin
  UserLanguagePage := CreateInputOptionPage(wpWelcome,
            ExpandConstant('{cm:JvmUserLanguageCaption}'),
            ExpandConstant('{cm:JvmUserLanguageDescription}'),
            ExpandConstant('{cm:JvmUserLanguageSubCaption}'),
            true, { Exclusive }
            false { ListBox }
  );

  UserLanguagePage.Add(ExpandConstant('{cm:JvmUserLanguageOptionAuto}'));
  UserLanguagePage.Add(ExpandConstant('{cm:JvmUserLanguageOptionDe}'));
  UserLanguagePage.Add(ExpandConstant('{cm:JvmUserLanguageOptionEn}'));

  UserLanguagePage.Values[GetUserLanguageIndex()] := true;
end;

procedure CreateMaximumMemoryPage();
begin
  MaximumMemoryPage := CreateInputOptionPage(UserLanguagePage.ID,
            ExpandConstant('{cm:JvmXmxCaption}'),
            ExpandConstant('{cm:JvmXmxDescription}'),
            ExpandConstant('{cm:JvmXmxSubCaption}'),
            true, { Exclusive }
            false { ListBox }
  );

  MaximumMemoryPage.Add(ExpandConstant('{cm:JvmXmx500Mb}'));
  MaximumMemoryPage.Add(ExpandConstant('{cm:JvmXmx750Mb}'));
  MaximumMemoryPage.Add(ExpandConstant('{cm:JvmXmx1Gb}'));

  MaximumMemoryPage.Values[GetMaximumMemoryIndex()] := true;
end;

procedure SetUserLanguage();
begin
  case UserLanguagePage.SelectedValueIndex of
    1 : UserLanguage := ' -Duser.language=de';
    2 : UserLanguage := ' -Duser.language=en';
    else UserLanguage := '';
  end;

  StoreUserLanguageIndex(UserLanguagePage.SelectedValueIndex);
end;

procedure SetMaximumMemory();
begin
  case MaximumMemoryPage.SelectedValueIndex of
    0 : MaximumMemory := '500m';
    1 : MaximumMemory := '750m';
    2 : MaximumMemory := '1g';
    else MaximumMemory := '750m';
  end;

  StoreMaximumMemoryIndex(MaximumMemoryPage.SelectedValueIndex);
end;

procedure CreateBatchFile();
var
  BatchFileName: String;
  DirectoryName: String;
  CommandLine: String;
begin
  DirectoryName := ExpandConstant('{app}');
  BatchFileName := DirectoryName + '\JPhotoTagger.bat';
  CommandLine := 'start javaw -jar -Xms30m -Xmx' + MaximumMemory + UserLanguage + ' "' + DirectoryName + '\JPhotoTagger.jar"';
  SaveStringToFile(BatchFileName, CommandLine, False);
end;

procedure InitializeWizard();
begin
  CreateUserLanguagePage();
  CreateMaximumMemoryPage();
end;

function NextButtonClick(CurPageID: Integer): Boolean;
begin
  case CurPageID of
    UserLanguagePage.ID : SetUserLanguage();
    MaximumMemoryPage.ID : SetMaximumMemory();
  end;

  Result := True;
end;

