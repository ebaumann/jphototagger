[Setup]
AppName=JPhotoTagger
AppVerName=JPhotoTagger 0.12.0
AppVersion=0.12.0
AppPublisher=Elmar Baumann <eb@elmar-baumann.de>
AppPublisherURL=http://www.jphototagger.org/
AppSupportURL=mailto:support@jphototagger.org
AppUpdatesURL=http://www.jphototagger.org/download.html
AppComments=Photo Manager
DefaultDirName={pf}\JPhotoTagger
DefaultGroupName=JPhotoTagger
AllowNoIcons=yes
OutputDir=D:\projekte\JPhotoTagger\main-repository\Website\dist
OutputBaseFilename=JPhotoTagger-setup
SetupIconFile=D:\projekte\JPhotoTagger\Support\res\app-icon-exports\JPhotoTagger.ico
Compression=lzma
SolidCompression=yes
UninstallDisplayIcon={app}\unins000.exe

[Languages]
Name: "german"; MessagesFile: "compiler:Languages\German.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "D:\projekte\JPhotoTagger\main-repository\Program\dist\JPhotoTagger.jar"; DestDir: "{app}"; AfterInstall: CreateBatchFile; Flags: ignoreversion
Source: "D:\projekte\JPhotoTagger\main-repository\Program\dist\lib\*"; DestDir: "{app}\lib"; Flags: ignoreversion
Source: "D:\projekte\JPhotoTagger\Support\res\app-icon-exports\JPhotoTagger.ico"; DestDir: "{app}"; Flags: ignoreversion
Source: "D:\projekte\JPhotoTagger\dist_files\manual\Manual_de.pdf"; DestDir: "{app}"; Flags: ignoreversion
Source: "D:\projekte\JPhotoTagger\dist_files\script\rotatejpg.sh"; DestDir: "{app}\scripts"
Source: "D:\projekte\JPhotoTagger\dist_files\script\thumbnail2stdoutwin.bat"; DestDir: "{app}\scripts"
Source: "D:\projekte\JPhotoTagger\dist_files\script\thumbnail2stdoutwin.sh"; DestDir: "{app}\scripts"

[Icons]
Name: "{group}\JPhotoTagger"; Filename: "{app}\JPhotoTagger.bat"; IconFilename: "{app}\JPhotoTagger.ico"; Flags: runminimized
Name: "{group}\Handbuch (PDF)"; Filename: "{app}\Manual_de.pdf"
Name: "{group}\{cm:ProgramOnTheWeb,JPhotoTagger}"; Filename: "http://www.jphototagger.org/"; IconFilename: "{app}\JPhotoTagger.ico"
Name: "{commondesktop}\JPhotoTagger"; Filename: "{app}\JPhotoTagger.bat"; Tasks: desktopicon; IconFilename: "{app}\JPhotoTagger.ico"; Flags: runminimized

[Run]
Filename: "{app}\JPhotoTagger.bat"; Description: "{cm:LaunchProgram,JPhotoTagger}"; Flags: nowait postinstall skipifsilent runminimized

[Messages]
WelcomeLabel2=[name/ver] wird auf Ihren Computer installiert.
FinishedLabel=Fertig. Bitte denken Sie daran, dass Java installiert sein muss!

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
            'Sprache (Language)',
            'Sprache von JPhotoTagger (GUI Language)',
            'Sprache der Benutzeroberfläche von JPhotoTagger (GUI Language of JPhotoTagger)',
            true,
            false);

  UserLanguagePage.Add('Automatisch');
  UserLanguagePage.Add('Deutsch');
  UserLanguagePage.Add('English');

  UserLanguagePage.Values[GetUserLanguageIndex()] := true;
end;

procedure CreateMaximumMemoryPage();
begin
  MaximumMemoryPage := CreateInputOptionPage(UserLanguagePage.ID,
            'Maximaler Arbeitsspeicher (Maximum Memory)',
            'Maximaler Arbeitsspeicher für JPhotoTagger (Maximum Memory)',
            'Maximaler Arbeitsspeicher für JPhotoTagger (Maximum Memory for JPhotoTagger)',
            true,
            false);

  MaximumMemoryPage.Add('500 Megabyte');
  MaximumMemoryPage.Add('750 Megabyte');
  MaximumMemoryPage.Add('1 Gigabyte');

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

