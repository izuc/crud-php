package software.crud.MySQL;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import software.crud.Models.*;
import software.crud.Utility.*;
import com.ibm.icu.text.PluralRules;
import com.google.common.base.CaseFormat;

public class PHP_APIMySQL {
	private List<Exception> exList;
	private Map<String, FinalQueryData> finalDataDic;
	private String templateFolder;
	private String templateFolderSeparator;
	private String destinationFolder;
	private String packageName;
	private ArrayList<CrudMessage> messages;
	private MySQLDBHelper mysqlDB;

	public PHP_APIMySQL(String packageName) {
		this.destinationFolder = System.getProperty("user.dir") + File.separator + packageName;
		this.packageName = packageName;
		this.exList = new ArrayList<>();
		this.templateFolder = "php-api";
		this.templateFolderSeparator = "\\\\";
		this.messages = new ArrayList<>();
	}

	public String getTemplateFolder() {
		return templateFolder;
	}

	public String getDestinationFolder() {
		return destinationFolder;
	}

	public String getProjectName() {
		String[] packageParts = packageName.split("\\.");
		return packageParts[packageParts.length - 1];
	}

	public ArrayList<CrudMessage> getMessages() {
		return messages;
	}

	private void logMessage(String message, boolean isSuccess) {
		messages.add(new CrudMessage(message, isSuccess));
	}

	private String createPath(String filePathString, boolean isTemplate) {
		String separator = isTemplate ? templateFolderSeparator : File.separator;
		String path = isTemplate ? templateFolder : destinationFolder;
		String[] parts = filePathString.split(",");

		for (String part : parts) {
			if (part != null && !part.trim().isEmpty()) {
				path = path + separator + part.trim();
			}
		}

		if (!isTemplate) {
			File file = new File(path);
			if (!file.exists()) {
				File parentDir = file.getParentFile();
				if (parentDir != null && !parentDir.exists()) {
					parentDir.mkdirs();
				}
			}
		}

		return path;
	}

	private String createDirectory() {
		String projectDirectory = destinationFolder;
		File directory = new File(projectDirectory);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		return projectDirectory;
	}

	private void updateRoutes() {
		try {
			StringBuilder routes = new StringBuilder();
			routes.append("<?php\n\n")
					.append("use Psr\\Http\\Message\\ResponseInterface as Response;\n")
					.append("use Psr\\Http\\Message\\ServerRequestInterface as Request;\n")
					.append("use Slim\\App;\n")
					.append("use App\\Middleware\\AuthMiddleware;\n\n")
					.append("return function (App $app) {\n")
					.append("    // User routes\n")
					.append("    $app->post('/register', \\App\\Controllers\\AuthController::class . ':register');\n")
					.append("    $app->post('/login', \\App\\Controllers\\AuthController::class . ':login');\n\n")
					.append("    // Protected routes\n")
					.append("    $app->get('/user', \\App\\Controllers\\UserController::class . ':getUser')->add(AuthMiddleware::class);\n\n")
					.append("    // Public routes\n")
					.append("    $app->get('/', function (Request $request, Response $response) {\n")
					.append("        $response->getBody().write(\"Welcome to CRUD PHP API!\");\n")
					.append("        return $response;\n")
					.append("    });\n")
					.append("    $app->get('/test', function (Request $request, Response $response) {\n")
					.append("        $response->getBody().write(\"Test route is working!\");\n")
					.append("        return $response;\n")
					.append("    });\n\n");

			for (Map.Entry<String, FinalQueryData> entry : finalDataDic.entrySet()) {
				String tableName = entry.getKey();
				String singularModelName = toModelName(tableName, true);
				String pluralModelName = toModelName(tableName, false);

				String singularModelNameCamel = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, singularModelName);
				String pluralModelNameCamel = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, pluralModelName);

				routes.append("    // ").append(singularModelName).append(" routes\n")
						.append("    $app->get('/").append(pluralModelNameCamel).append("', \\App\\Controllers\\")
						.append(singularModelName).append("Controller::class . ':getAll');\n")
						.append("    $app->get('/").append(singularModelNameCamel)
						.append("/{").append(entry.getValue().getPrimaryKeys().get(0).getFieldName())
						.append("}', \\App\\Controllers\\")
						.append(singularModelName).append("Controller::class . ':get');\n")
						.append("    $app->post('/").append(singularModelNameCamel).append("', \\App\\Controllers\\")
						.append(singularModelName).append("Controller::class . ':create');\n")
						.append("    $app->put('/").append(singularModelNameCamel)
						.append("/{").append(entry.getValue().getPrimaryKeys().get(0).getFieldName())
						.append("}', \\App\\Controllers\\")
						.append(singularModelName).append("Controller::class . ':update');\n")
						.append("    $app->delete('/").append(singularModelNameCamel)
						.append("/{").append(entry.getValue().getPrimaryKeys().get(0).getFieldName())
						.append("}', \\App\\Controllers\\")
						.append(singularModelName).append("Controller::class . ':delete');\n\n");
			}

			routes.append("};\n");
			String path = createPath("routes,api.php", false);
			CopyDir.writeWithoutBOM(path, routes.toString());
			logMessage("Routes updated successfully!", true);
		} catch (IOException ex) {
			logMessage("Error updating routes: " + ex.getMessage(), false);
			ex.printStackTrace();
		}
	}

	public List<Exception> createPHPApp(CodeInput<FinalQueryData> phpInput) {
		createDirectory();
		finalDataDic = phpInput.getFinalDataDic();

		updateRoutes();
		createPHPModelFile();
		createPHPControllerFile();
		createPHPServiceFile();

		logMessage("----- PHP App Generated -----", false);
		logMessage("Please check the generated code at: " + destinationFolder, false);
		return exList;
	}

	private void createPHPModelFile() {
		for (Map.Entry<String, FinalQueryData> item : finalDataDic.entrySet()) {
			try {
				List<ColumnModel> columns = item.getValue().getSelectQueryData().getColumnList();
				String key = item.getKey();
				String modelName = toModelName(key, true);
				String path = createPath("app,Models," + modelName + ".php", false);

				StringBuilder classContent = new StringBuilder();
				classContent.append("<?php\n\n");
				classContent.append("namespace App\\Models;\n\n");
				classContent.append("use Illuminate\\Database\\Eloquent\\Model;\n\n");
				classContent.append("class ").append(modelName).append(" extends Model\n");
				classContent.append("{\n");
				classContent.append("    protected $table = '").append(key).append("';\n");
				classContent.append("    protected $primaryKey = '")
						.append(item.getValue().getPrimaryKeys().get(0).getFieldName()).append("';\n\n");
				classContent.append("    protected $fillable = [\n");
				for (ColumnModel column : columns) {
					classContent.append("        '").append(column.getField()).append("',\n");
				}
				classContent.append("    ];\n\n");
				classContent.append("    protected $hidden = [\n");
				classContent.append("        'password',\n");
				classContent.append("    ];\n");
				classContent.append("}\n");

				CopyDir.writeWithoutBOM(path, classContent.toString());

				logMessage("PHP Model class for " + modelName + " generated successfully!", true);
			} catch (Exception ex) {
				logMessage("Error generating PHP model for " + item.getKey() + ": " + ex.getMessage(), false);
				ex.printStackTrace();
			}
		}
	}

	private void createPHPControllerFile() {
		for (Map.Entry<String, FinalQueryData> item : finalDataDic.entrySet()) {
			try {
				String key = item.getKey();
				String modelName = toModelName(key, true);
				String path = createPath("app,Controllers," + modelName + "Controller.php", false);

				StringBuilder classContent = new StringBuilder();
				classContent.append("<?php\n\n");
				classContent.append("namespace App\\Controllers;\n\n");
				classContent.append("use Psr\\Http\\Message\\ResponseInterface as Response;\n");
				classContent.append("use Psr\\Http\\Message\\ServerRequestInterface as Request;\n");
				classContent.append("use App\\Services\\").append(modelName).append("Service;\n");
				classContent.append("use App\\Models\\").append(modelName).append(";\n\n");
				classContent.append("class ").append(modelName).append("Controller\n");
				classContent.append("{\n");
				classContent.append("    private $").append(toCamelCase(modelName)).append("Service;\n\n");
				classContent.append("    public function __construct(").append(modelName).append("Service $")
						.append(toCamelCase(modelName)).append("Service)\n");
				classContent.append("    {\n");
				classContent.append("        $this->").append(toCamelCase(modelName)).append("Service = $")
						.append(toCamelCase(modelName)).append("Service;\n");
				classContent.append("    }\n\n");
				classContent.append("    public function getAll(Request $request, Response $response): Response\n");
				classContent.append("    {\n");
				classContent.append("        $items = $this->").append(toCamelCase(modelName))
						.append("Service->getAll();\n");
				classContent.append("        $response->getBody().write(json_encode($items));\n");
				classContent.append(
						"        return $response->withHeader('Content-Type', 'application/json').withStatus(200);\n");
				classContent.append("    }\n\n");
				classContent.append("    public function get(Request $request, Response $response, $args): Response\n");
				classContent.append("    {\n");
				classContent.append("        $").append(item.getValue().getPrimaryKeys().get(0).getFieldName())
						.append(" = $args['").append(item.getValue().getPrimaryKeys().get(0).getFieldName())
						.append("'];\n");
				classContent.append("        $item = $this->").append(toCamelCase(modelName))
						.append("Service->get($").append(item.getValue().getPrimaryKeys().get(0).getFieldName())
						.append(");\n");
				classContent.append("        $response->getBody().write(json_encode($item));\n");
				classContent.append(
						"        return $response->withHeader('Content-Type', 'application/json').withStatus(200);\n");
				classContent.append("    }\n\n");
				classContent.append("    public function create(Request $request, Response $response): Response\n");
				classContent.append("    {\n");
				classContent.append("        $data = $request->getParsedBody();\n");
				classContent.append("        $item = $this->").append(toCamelCase(modelName))
						.append("Service->create($data);\n");
				classContent.append("        $response->getBody().write(json_encode($item));\n");
				classContent.append(
						"        return $response->withHeader('Content-Type', 'application/json').withStatus(201);\n");
				classContent.append("    }\n\n");
				classContent
						.append("    public function update(Request $request, Response $response, $args): Response\n");
				classContent.append("    {\n");
				classContent.append("        $").append(item.getValue().getPrimaryKeys().get(0).getFieldName())
						.append(" = $args['").append(item.getValue().getPrimaryKeys().get(0).getFieldName())
						.append("'];\n");
				classContent.append("        $data = $request->getParsedBody();\n");
				classContent.append("        $item = $this->").append(toCamelCase(modelName))
						.append("Service->update($").append(item.getValue().getPrimaryKeys().get(0).getFieldName())
						.append(", $data);\n");
				classContent.append("        $response->getBody().write(json_encode($item));\n");
				classContent.append(
						"        return $response->withHeader('Content-Type', 'application/json').withStatus(200);\n");
				classContent.append("    }\n\n");
				classContent
						.append("    public function delete(Request $request, Response $response, $args): Response\n");
				classContent.append("    {\n");
				classContent.append("        $").append(item.getValue().getPrimaryKeys().get(0).getFieldName())
						.append(" = $args['").append(item.getValue().getPrimaryKeys().get(0).getFieldName())
						.append("'];\n");
				classContent.append("        $this->").append(toCamelCase(modelName)).append("Service->delete($")
						.append(item.getValue().getPrimaryKeys().get(0).getFieldName()).append(");\n");
				classContent.append(
						"        return $response->withHeader('Content-Type', 'application/json').withStatus(204);\n");
				classContent.append("    }\n");
				classContent.append("}\n");

				CopyDir.writeWithoutBOM(path, classContent.toString());

				logMessage("PHP Controller class for " + modelName + " generated successfully!", true);
			} catch (Exception ex) {
				logMessage("Error generating PHP controller for " + item.getKey() + ": " + ex.getMessage(), false);
				ex.printStackTrace();
			}
		}
	}

	private void createPHPServiceFile() {
		for (Map.Entry<String, FinalQueryData> item : finalDataDic.entrySet()) {
			try {
				String key = item.getKey();
				String modelName = toModelName(key, true);
				String path = createPath("app,Services," + modelName + "Service.php", false);

				StringBuilder classContent = new StringBuilder();
				classContent.append("<?php\n\n");
				classContent.append("namespace App\\Services;\n\n");
				classContent.append("use App\\Models\\").append(modelName).append(";\n\n");
				classContent.append("class ").append(modelName).append("Service\n");
				classContent.append("{\n");
				classContent.append("    public function getAll()\n");
				classContent.append("    {\n");
				classContent.append("        return ").append(modelName).append("::all();\n");
				classContent.append("    }\n\n");
				classContent.append("    public function get($")
						.append(item.getValue().getPrimaryKeys().get(0).getFieldName()).append(")\n");
				classContent.append("    {\n");
				classContent.append("        return ").append(modelName).append("::find($")
						.append(item.getValue().getPrimaryKeys().get(0).getFieldName()).append(");\n");
				classContent.append("    }\n\n");
				classContent.append("    public function create($data)\n");
				classContent.append("    {\n");
				classContent.append("        return ").append(modelName).append("::create($data);\n");
				classContent.append("    }\n\n");
				classContent.append("    public function update($")
						.append(item.getValue().getPrimaryKeys().get(0).getFieldName()).append(", $data)\n");
				classContent.append("    {\n");
				classContent.append("        $item = ").append(modelName).append("::find($")
						.append(item.getValue().getPrimaryKeys().get(0).getFieldName()).append(");\n");
				classContent.append("        if ($item) {\n");
				classContent.append("            $item->update($data);\n");
				classContent.append("            return $item;\n");
				classContent.append("        }\n");
				classContent.append("        return null;\n");
				classContent.append("    }\n\n");
				classContent.append("    public function delete($")
						.append(item.getValue().getPrimaryKeys().get(0).getFieldName()).append(")\n");
				classContent.append("    {\n");
				classContent.append("        $item = ").append(modelName).append("::find($")
						.append(item.getValue().getPrimaryKeys().get(0).getFieldName()).append(");\n");
				classContent.append("        if ($item) {\n");
				classContent.append("            $item->delete();\n");
				classContent.append("        }\n");
				classContent.append("    }\n");
				classContent.append("}\n");

				CopyDir.writeWithoutBOM(path, classContent.toString());

				logMessage("PHP Service class for " + modelName + " generated successfully!", true);
			} catch (Exception ex) {
				logMessage("Error generating PHP service for " + item.getKey() + ": " + ex.getMessage(), false);
				ex.printStackTrace();
			}
		}
	}

	// Method to create the index.php file in the public/ folder
	private void createIndexFile(String authTable) {
		try {
			String path = createPath("public,index.php", false);

			StringBuilder indexContent = new StringBuilder();
			indexContent.append("<?php\n\n");
			indexContent.append("ini_set('display_errors', 1);\n");
			indexContent.append("ini_set('display_startup_errors', 1);\n");
			indexContent.append("error_reporting(E_ALL);\n\n");
			indexContent.append("use Psr\\Http\\Message\\ResponseInterface as Response;\n");
			indexContent.append("use Psr\\Http\\Message\\ServerRequestInterface as Request;\n");
			indexContent.append("use Selective\\BasePath\\BasePathMiddleware;\n");
			indexContent.append("use DI\\ContainerBuilder;\n");
			indexContent.append("use DI\\Bridge\\Slim\\Bridge as SlimAppFactory;\n");
			indexContent.append("use Psr\\Container\\ContainerInterface;\n");
			indexContent.append("use Slim\\Factory\\AppFactory;\n");
			indexContent.append("use App\\Middleware\\AuthMiddleware;\n");
			indexContent.append("use App\\Services\\").append(toModelName(authTable, true)).append("Service;\n\n");
			indexContent.append("require __DIR__ . '/../vendor/autoload.php';\n\n");
			indexContent.append("// Load environment variables from .env file\n");
			indexContent.append("try {\n");
			indexContent.append("    $dotenv = Dotenv\\Dotenv::createImmutable(__DIR__ . '/..');\n");
			indexContent.append("    $dotenv->safeLoad();\n");
			indexContent.append("} catch (Exception $e) {\n");
			indexContent.append("    echo 'Dotenv could not be initialized: ', $e->getMessage(), \"\\n\";\n");
			indexContent.append("    exit();\n");
			indexContent.append("}\n\n");
			indexContent.append("// Create Container using PHP-DI\n");
			indexContent.append("$containerBuilder = new ContainerBuilder();\n\n");
			indexContent.append("$containerBuilder->addDefinitions([\n");
			indexContent.append("    'config' => [\n");
			indexContent.append("        'name' => $_ENV['APP_NAME'],\n");
			indexContent.append("        'env' => $_ENV['APP_ENV'],\n");
			indexContent.append("        'debug' => $_ENV['APP_DEBUG'] ?: false,\n");
			indexContent.append("        'url' => $_ENV['APP_URL'],\n");
			indexContent.append("        'timezone' => 'UTC',\n");
			indexContent.append("        'locale' => 'en',\n");
			indexContent.append("        'fallback_locale' => 'en',\n");
			indexContent.append("        'key' => $_ENV['APP_KEY'],\n");
			indexContent.append("        'jwt' => $_ENV['JWT_SECRET_KEY'],\n");
			indexContent.append("        'cipher' => 'AES-256-CBC',\n\n");
			indexContent.append("        // Database Configuration\n");
			indexContent.append("        'database' => [\n");
			indexContent.append("            'driver' => $_ENV['DB_DRIVER'],\n");
			indexContent.append("            'host' => $_ENV['DB_HOST'],\n");
			indexContent.append("            'port' => $_ENV['DB_PORT'],\n");
			indexContent.append("            'database' => $_ENV['DB_DATABASE'],\n");
			indexContent.append("            'username' => $_ENV['DB_USERNAME'],\n");
			indexContent.append("            'password' => $_ENV['DB_PASSWORD'],\n");
			indexContent.append("            'charset' => 'utf8mb4',\n");
			indexContent.append("            'collation' => 'utf8mb4_unicode_ci',\n");
			indexContent.append("            'prefix' => '',\n");
			indexContent.append("        ],\n");
			indexContent.append("    ],\n\n");
			indexContent.append("    // Register the ").append(toModelName(authTable, true))
					.append("Service for dependency injection\n");
			indexContent.append("    ").append(toModelName(authTable, true)).append("Service::class => \\DI\\autowire(")
					.append(toModelName(authTable, true)).append("Service::class),\n");
			indexContent.append("]);\n\n");
			indexContent.append("// Register other dependencies\n");
			indexContent.append("$containerBuilder->addDefinitions([\n");
			indexContent.append("    'App\\Services\\AuthService' => function (ContainerInterface $container) {\n");
			indexContent.append("        return new \\App\\Services\\AuthService($container);\n");
			indexContent.append("    },\n");
			indexContent.append("]);\n\n");
			indexContent.append("$container = $containerBuilder->build();\n");
			indexContent.append("AppFactory::setContainer($container);\n");
			indexContent.append("$app = AppFactory::create();\n\n");
			indexContent.append("// Add the BasePathMiddleware\n");
			indexContent.append("$app->add(new BasePathMiddleware($app));\n\n");
			indexContent.append("// Create Eloquent Capsule Manager\n");
			indexContent.append("$capsule = new \\Illuminate\\Database\\Capsule\\Manager;\n");
			indexContent.append("$capsule->addConnection($container->get('config')['database']);\n");
			indexContent.append("$capsule->setAsGlobal();\n");
			indexContent.append("$capsule->bootEloquent();\n\n");
			indexContent.append("// Add error middleware\n");
			indexContent.append("$errorMiddleware = $app->addErrorMiddleware(true, true, true);\n\n");
			indexContent.append("// Register routes\n");
			indexContent.append("(require __DIR__ . '/../routes/api.php')($app);\n\n");
			indexContent.append("// Add body parsing middleware\n");
			indexContent.append("$app->addBodyParsingMiddleware();\n\n");
			indexContent.append("// Add CORS middleware\n");
			indexContent.append(
					"$app->add(function (Request $request, \\Psr\\Http\\Server\\RequestHandlerInterface $handler) {\n");
			indexContent.append("    $response = $handler->handle($request);\n");
			indexContent.append("    return $response\n");
			indexContent.append("        ->withHeader('Access-Control-Allow-Origin', '*')\n");
			indexContent.append(
					"        ->withHeader('Access-Control-Allow-Headers', 'X-Requested-With, Content-Type, Accept, Origin, Authorization')\n");
			indexContent.append(
					"        ->withHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, PATCH, OPTIONS');\n");
			indexContent.append("});\n\n");
			indexContent.append("// Run the app\n");
			indexContent.append("$app->run();\n");

			CopyDir.writeWithoutBOM(path, indexContent.toString());

			logMessage("index.php file generated successfully!", true);
		} catch (Exception ex) {
			logMessage("Error generating index.php file: " + ex.getMessage(), false);
			ex.printStackTrace();
		}
	}

	// Call the createIndexFile method within the automator method
	// Call the createAuthMiddlewareFile method within the automator method
	public CodeInput<FinalQueryData> automator(String packageName, List<String> selectedTables, MySQLDBHelper mySQLDB,
			String authTable, String authUsernameField, String authPasswordField, String authPrimaryKey,
			String appName, String appUrl, String jwtSecret, String dbDriver,
			String dbCharset, String dbHost, String dbPort, String dbName,
			String dbUsername, String dbPassword) throws Exception {
		mysqlDB = mySQLDB;
		this.packageName = packageName;
		String text = createDirectory();
		logMessage("Project Folder Created: " + text, true);
		logMessage("Generating PHP Project...", true);
		logMessage("Copying Project File, Might take some time...", true);
		copyProject();
		logMessage("Finished Copying Project File", true);
		logMessage("Analyzing Database...", true);
		CodeInput<FinalQueryData> codeInput = new CodeInput<>();
		codeInput.setDestinationFolder(text);
		codeInput.setFinalDataDic(new HashMap<>());

		for (String item : selectedTables) {
			try {
				logMessage("Processing for Table => " + item, true);
				InsertUpdateQueryData insertUpdateQueryData = mysqlDB.getInsertUpdateQueryData(item);
				FinalQueryData finalQueryData = mysqlDB.buildLaravelQuery(item);
				finalQueryData.setInsertUpdateQueryData(insertUpdateQueryData);
				codeInput.getFinalDataDic().put(item, finalQueryData);
			} catch (Exception ex) {
				logMessage("Exception on table " + item + " - " + ex.getMessage(), false);
				StringBuilder sw = new StringBuilder();
				for (StackTraceElement ste : ex.getStackTrace()) {
					sw.append(ste.toString()).append("\n");
				}
				String exceptionAsString = sw.toString();
				logMessage(exceptionAsString, false);
			}
		}

		// Generate auth files if auth table is specified
		if (authTable != null && !authTable.isEmpty()) {
			createAuthFiles(authTable, authUsernameField, authPasswordField, authPrimaryKey);
		}

		// Create .env file with the given configuration
		createEnvFile(text, appName, appUrl, jwtSecret, dbDriver, dbCharset, dbHost, dbPort, dbName, dbUsername,
				dbPassword);

		// Create the index.php file in the public/ folder
		createIndexFile(authTable);

		// Create the AuthMiddleware file
		createAuthMiddlewareFile();

		return codeInput;
	}

	private void createEnvFile(String projectDirectory, String appName, String appUrl, String jwtSecret,
			String dbDriver, String dbCharset, String dbHost, String dbPort, String dbName,
			String dbUsername, String dbPassword) {
		String envContent = String.format(
				"APP_NAME=%s\n" +
						"APP_URL=%s\n" +
						"JWT_SECRET=%s\n" +
						"DB_CONNECTION=%s\n" +
						"DB_HOST=%s\n" +
						"DB_PORT=%s\n" +
						"DB_DATABASE=%s\n" +
						"DB_USERNAME=%s\n" +
						"DB_PASSWORD=%s\n" +
						"DB_CHARSET=%s\n",
				appName, appUrl, jwtSecret, dbDriver, dbHost, dbPort, dbName, dbUsername, dbPassword, dbCharset);

		try (FileWriter writer = new FileWriter(projectDirectory + "/.env")) {
			writer.write(envContent);
			logMessage(".env file created successfully", true);
		} catch (IOException e) {
			logMessage("Error creating .env file: " + e.getMessage(), false);
		}
	}

	private void copyProject() {
		String sourceDirectory = getTemplateFolder();
		String targetDirectory = getDestinationFolder();

		try {
			CopyDir.copy(sourceDirectory, targetDirectory, getProjectName(), "ReactTemplate");
			logMessage("Project files copied successfully!", true);
		} catch (Exception e) {
			logMessage("Error occurred while copying project files: " + e.getMessage(), false);
			e.printStackTrace();
		}
	}

	public void createAuthFiles(String authTable, String authUsernameField, String authPasswordField,
			String authPrimaryKey) {
		createAuthModelFile(authTable, authUsernameField, authPasswordField, authPrimaryKey);
		createAuthControllerFile(authTable, authUsernameField, authPasswordField);
		createAuthServiceFile(authTable, authUsernameField, authPasswordField);
		updateEnvFile(authTable, authUsernameField, authPasswordField);
	}

	private void createAuthModelFile(String authTable, String authUsernameField, String authPasswordField,
			String authPrimaryKey) {
		try {
			String path = createPath("app,Models," + authTable + ".php", false);

			StringBuilder classContent = new StringBuilder();
			classContent.append("<?php\n\n");
			classContent.append("namespace App\\Models;\n\n");
			classContent.append("use Illuminate\\Database\\Eloquent\\Model;\n\n");
			classContent.append("class ").append(authTable).append(" extends Model\n");
			classContent.append("{\n");
			classContent.append("    protected $table = '").append(authTable).append("';\n");
			classContent.append("    protected $primaryKey = '").append(authPrimaryKey).append("';\n\n");
			classContent.append("    protected $fillable = [\n");
			classContent.append("        '").append(authUsernameField).append("',\n");
			classContent.append("        '").append(authPasswordField).append("',\n");
			classContent.append("    ];\n\n");
			classContent.append("    protected $hidden = [\n");
			classContent.append("        '").append(authPasswordField).append("',\n");
			classContent.append("    ];\n");
			classContent.append("}\n");

			CopyDir.writeWithoutBOM(path, classContent.toString());

			logMessage("Auth model file generated successfully!", true);
		} catch (Exception ex) {
			logMessage("Error generating auth model file: " + ex.getMessage(), false);
			ex.printStackTrace();
		}
	}

	private void createAuthControllerFile(String authTable, String authUsernameField, String authPasswordField) {
		try {
			String path = createPath("app,Controllers,AuthController.php", false);

			StringBuilder classContent = new StringBuilder();
			classContent.append("<?php\n\n");
			classContent.append("namespace App\\Controllers;\n\n");
			classContent.append("use App\\Services\\AuthService;\n");
			classContent.append("use Psr\\Http\\Message\\ResponseInterface as Response;\n");
			classContent.append("use Psr\\Http\\Message\\ServerRequestInterface as Request;\n\n");
			classContent.append("class AuthController\n");
			classContent.append("{\n");
			classContent.append("    private $authService;\n\n");
			classContent.append("    public function __construct(AuthService $authService)\n");
			classContent.append("    {\n");
			classContent.append("        $this->authService = $authService;\n");
			classContent.append("    }\n\n");
			classContent.append("    public function register(Request $request, Response $response): Response\n");
			classContent.append("    {\n");
			classContent.append("        try {\n");
			classContent.append("            $userData = $request->getParsedBody();\n");
			classContent.append("            $username = $userData['").append(authUsernameField).append("'];\n");
			classContent.append("            $password = $userData['").append(authPasswordField).append("'];\n");
			classContent.append("            $user = $this->authService->registerUser($username, $password);\n\n");
			classContent.append(
					"            $response->getBody().write(json_encode(['message' => 'User registered successfully', 'user' => $user]));\n");
			classContent.append(
					"            return $response->withHeader('Content-Type', 'application/json')->withStatus(201);\n");
			classContent.append("        } catch (\\Exception $e) {\n");
			classContent.append(
					"            $response->getBody().write(json_encode(['error' => 'Registration failed: ' . $e->getMessage()]));\n");
			classContent.append(
					"            return $response->withHeader('Content-Type', 'application/json')->withStatus(500);\n");
			classContent.append("        }\n");
			classContent.append("    }\n\n");
			classContent.append("    public function login(Request $request, Response $response): Response\n");
			classContent.append("    {\n");
			classContent.append("        try {\n");
			classContent.append("            $credentials = $request->getParsedBody();\n");
			classContent.append("            $username = $credentials['").append(authUsernameField).append("'];\n");
			classContent.append("            $password = $credentials['").append(authPasswordField).append("'];\n");
			classContent.append("            $user = $this->authService->loginUser($username, $password);\n\n");
			classContent.append(
					"            $response->getBody().write(json_encode(['message' => 'Login successful', 'token' => $user['token'], 'user' => $user['user']]));\n");
			classContent.append(
					"            return $response->withHeader('Content-Type', 'application/json')->withStatus(200);\n");
			classContent.append("        } catch (\\Exception $e) {\n");
			classContent.append(
					"            $response->getBody().write(json_encode(['error' => 'Login failed: ' . $e.getMessage()]));\n");
			classContent.append(
					"            return $response->withHeader('Content-Type', 'application/json')->withStatus(401);\n");
			classContent.append("        }\n");
			classContent.append("    }\n");
			classContent.append("}\n");

			CopyDir.writeWithoutBOM(path, classContent.toString());

			logMessage("Auth controller file generated successfully!", true);
		} catch (Exception ex) {
			logMessage("Error generating auth controller file: " + ex.getMessage(), false);
			ex.printStackTrace();
		}
	}

	private void createAuthServiceFile(String authTable, String authUsernameField, String authPasswordField) {
		try {
			String path = createPath("app,Services,AuthService.php", false);

			StringBuilder classContent = new StringBuilder();
			classContent.append("<?php\n\n");
			classContent.append("namespace App\\Services;\n\n");
			classContent.append("use App\\Models\\User;\n");
			classContent.append("use Firebase\\JWT\\JWT;\n");
			classContent.append("use Psr\\Container\\ContainerInterface;\n\n");
			classContent.append("class AuthService\n");
			classContent.append("{\n");
			classContent.append("    private $container;\n\n");
			classContent.append("    public function __construct(ContainerInterface $container)\n");
			classContent.append("    {\n");
			classContent.append("        $this->container = $container;\n");
			classContent.append("    }\n\n");
			classContent.append("    public function registerUser($username, $password)\n");
			classContent.append("    {\n");
			classContent.append("        try {\n");
			classContent.append("            $user = new User();\n");
			classContent.append("            $user->").append(authUsernameField).append(" = $username;\n");
			classContent.append("            $user->").append(authPasswordField)
					.append(" = password_hash($password, PASSWORD_DEFAULT);\n");
			classContent.append("            $user->save();\n");
			classContent.append("            return $user;\n");
			classContent.append("        } catch (\\Exception $e) {\n");
			classContent.append("            throw new \\Exception('Failed to register user: ' . $e.getMessage());\n");
			classContent.append("        }\n");
			classContent.append("    }\n\n");
			classContent.append("    public function loginUser($username, $password)\n");
			classContent.append("    {\n");
			classContent.append("        try {\n");
			classContent.append("            $user = User::where('").append(authUsernameField)
					.append("', $username).first();\n");
			classContent.append("            if (!$user || !password_verify($password, $user->")
					.append(authPasswordField).append(")) {\n");
			classContent.append("                throw new \\Exception('Invalid credentials');\n");
			classContent.append("            }\n\n");
			classContent.append("            return [\n");
			classContent.append("                'token' => $this->generateToken($user),\n");
			classContent.append("                'user' => $user\n");
			classContent.append("            ];\n");
			classContent.append("        } catch (\\Exception $e) {\n");
			classContent.append("            throw new \\Exception('Failed to login user: ' . $e.getMessage());\n");
			classContent.append("        }\n");
			classContent.append("    }\n\n");
			classContent.append("    private function generateToken(User $user)\n");
			classContent.append("    {\n");
			classContent.append("        $payload = [\n");
			classContent.append("            'user_id' => $user->id,\n");
			classContent.append("            '").append(authUsernameField).append("' => $user->")
					.append(authUsernameField).append(",\n");
			classContent.append("            'exp' => time() + (60 * 60 * 24), // Token expires in 24 hours\n");
			classContent.append("        ];\n\n");
			classContent.append("        $secretKey = $this->container.get('config')['jwt'];\n");
			classContent.append("        return JWT::encode($payload, $secretKey, 'HS256');\n");
			classContent.append("    }\n");
			classContent.append("}\n");

			CopyDir.writeWithoutBOM(path, classContent.toString());

			logMessage("Auth service file generated successfully!", true);
		} catch (Exception ex) {
			logMessage("Error generating auth service file: " + ex.getMessage(), false);
			ex.printStackTrace();
		}
	}

	private void updateEnvFile(String authTable, String authUsernameField, String authPasswordField) {
		try {
			String path = createPath(".env", false);
			File envFile = new File(path);

			StringBuilder envContent = new StringBuilder();
			if (envFile.exists()) {
				try (Scanner scanner = new Scanner(envFile)) {
					while (scanner.hasNextLine()) {
						String line = scanner.nextLine();
						envContent.append(line).append("\n");
					}
				}
			}

			envContent.append("\n# Auth Configuration\n");
			envContent.append("AUTH_TABLE=").append(authTable).append("\n");
			envContent.append("AUTH_USERNAME_FIELD=").append(authUsernameField).append("\n");
			envContent.append("AUTH_PASSWORD_FIELD=").append(authPasswordField).append("\n");

			try (FileWriter writer = new FileWriter(envFile)) {
				writer.write(envContent.toString());
			}

			logMessage(".env file updated successfully!", true);
		} catch (Exception ex) {
			logMessage("Error updating .env file: " + ex.getMessage(), false);
			ex.printStackTrace();
		}
	}

	private void createAuthMiddlewareFile() {
		try {
			String path = createPath("app,Middleware,AuthMiddleware.php", false);

			StringBuilder classContent = new StringBuilder();
			classContent.append("<?php\n\n");
			classContent.append("namespace App\\Middleware;\n\n");
			classContent.append("use Psr\\Http\\Message\\ResponseInterface as Response;\n");
			classContent.append("use Psr\\Http\\Message\\ServerRequestInterface as Request;\n");
			classContent.append("use Psr\\Http\\Server\\MiddlewareInterface;\n");
			classContent.append("use Psr\\Http\\Server\\RequestHandlerInterface as RequestHandler;\n");
			classContent.append("use Psr\\Container\\ContainerInterface;\n");
			classContent.append("use Firebase\\JWT\\JWT;\n");
			classContent.append("use Firebase\\JWT\\Key;\n\n");
			classContent.append("class AuthMiddleware implements MiddlewareInterface\n");
			classContent.append("{\n");
			classContent.append("    private $container;\n\n");
			classContent.append("    public function __construct(ContainerInterface $container)\n");
			classContent.append("    {\n");
			classContent.append("        $this->container = $container;\n");
			classContent.append("    }\n\n");
			classContent.append("    public function process(Request $request, RequestHandler $handler): Response\n");
			classContent.append("    {\n");
			classContent.append("        $authorizationHeader = $request->getHeaderLine('Authorization');\n\n");
			classContent.append("        if (empty($authorizationHeader)) {\n");
			classContent.append("            $response = new \\Slim\\Psr7\\Response();\n");
			classContent.append(
					"            $response->getBody().write(json_encode(['error' => 'Missing authorization header']));\n");
			classContent.append(
					"            return $response->withHeader('Content-Type', 'application/json')->withStatus(401);\n");
			classContent.append("        }\n\n");
			classContent.append("        $token = str_replace('Bearer ', '', $authorizationHeader);\n\n");
			classContent.append("        try {\n");
			classContent.append("            $decodedToken = $this->verifyAndDecodeToken($token);\n");
			classContent.append("            $userId = $decodedToken['user_id'];\n");
			classContent.append("            if (!$userId) {\n");
			classContent.append("                throw new \\Exception('Invalid token: User ID not found');\n");
			classContent.append("            }\n");
			classContent.append("            $request = $request->withAttribute('userId', $userId);\n");
			classContent.append("            return $handler->handle($request);\n");
			classContent.append("        } catch (\\Exception $e) {\n");
			classContent.append("            $response = new \\Slim\\Psr7\\Response();\n");
			classContent.append(
					"            $response->getBody().write(json_encode(['error' => 'Invalid token: ' . $e.getMessage()]));\n");
			classContent.append(
					"            return $response->withHeader('Content-Type', 'application/json')->withStatus(401);\n");
			classContent.append("        }\n");
			classContent.append("    }\n\n");
			classContent.append("    private function verifyAndDecodeToken(string $token)\n");
			classContent.append("    {\n");
			classContent.append("        $secretKey = $this->container->get('config')['jwt'];\n\n");
			classContent.append("        if (!$secretKey) {\n");
			classContent.append("            throw new \\Exception('Secret key not configured');\n");
			classContent.append("        }\n\n");
			classContent.append("        try {\n");
			classContent.append("            $decoded = JWT::decode($token, new Key($secretKey, 'HS256'));\n");
			classContent.append("            return (array) $decoded;\n");
			classContent.append("        } catch (\\Exception $e) {\n");
			classContent.append("            throw new \\Exception('Invalid token: ' . $e.getMessage());\n");
			classContent.append("        }\n");
			classContent.append("    }\n");
			classContent.append("}\n");

			CopyDir.writeWithoutBOM(path, classContent.toString());

			logMessage("Auth middleware file generated successfully!", true);
		} catch (Exception ex) {
			logMessage("Error generating auth middleware file: " + ex.getMessage(), false);
			ex.printStackTrace();
		}
	}

	public static String pluralize(String word) {
		PluralRules pluralRules = PluralRules.forLocale(Locale.ENGLISH);
		String pluralForm = pluralRules.select(2);
		return word + (pluralForm.equals("one") ? "" : "s");
	}

	public static String depluralize(String word) {
		return Inflector.depluralize(word);
	}

	public static String toModelName(String tableName, boolean depluralize) {
		String camelCase = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tableName);
		return depluralize ? Inflector.depluralize(camelCase) : camelCase;
	}

	public static String toTableName(String modelName) {
		String underscore = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, modelName);
		return pluralize(underscore);
	}

	private static class Inflector {
		private static final Map<String, String> plurals = new LinkedHashMap<>();
		private static final Map<String, String> singulars = new LinkedHashMap<>();

		static {
			plurals.put("$", "s");
			plurals.put("s$", "s");
			plurals.put("(ax|test)is$", "$1es");
			plurals.put("(octop|vir)us$", "$1i");
			plurals.put("(alias|status)$", "$1es");
			plurals.put("(bu)s$", "$1ses");
			plurals.put("(buffal|tomat|volcan)o$", "$1oes");
			plurals.put("([ti])um$", "$1a");
			plurals.put("sis$", "ses");
			plurals.put("(?:([^f])fe|([lr])f)$", "$1$2ves");
			plurals.put("(hive)$", "$1s");
			plurals.put("([^aeiouy]|qu)y$", "$1ies");
			plurals.put("(x|ch|ss|sh)$", "$1es");
			plurals.put("(matr|vert|ind)(?:ix|ex)$", "$1ices");
			plurals.put("([m|l])ouse$", "$1ice");
			plurals.put("^(ox)$", "$1en");
			plurals.put("(quiz)$", "$1zes");

			singulars.put("s$", "");
			singulars.put("(ss)$", "$1");
			singulars.put("(n)ews$", "$1ews");
			singulars.put("([ti])a$", "$1um");
			singulars.put("((a)naly|(b)a|(d)iagno|(p)arenthe|(p)rogno|(s)ynop|(t)he)ses$", "$1$2sis");
			singulars.put("(^analy)ses$", "$1sis");
			singulars.put("([^f])ves$", "$1fe");
			singulars.put("(hive)s$", "$1");
			singulars.put("(tive)s$", "$1");
			singulars.put("([lr])ves$", "$1f");
			singulars.put("([^aeiouy]|qu)ies$", "$1y");
			singulars.put("(s)eries$", "$1eries");
			singulars.put("(m)ovies$", "$1ovie");
			singulars.put("(x|ch|ss|sh)es$", "$1");
			singulars.put("([m|l])ice$", "$1ouse");
			singulars.put("(bus)(es)?$", "$1");
			singulars.put("(o)es$", "$1");
			singulars.put("(shoe)s$", "$1");
			singulars.put("(cris|ax|test)is$", "$1is");
			singulars.put("(cris|ax|test)es$", "$1is");
			singulars.put("(octop|vir)i$", "$1us");
			singulars.put("(alias|status)es$", "$1");
			singulars.put("^(ox)en", "$1");
			singulars.put("(vert|ind)ices$", "$1ex");
			singulars.put("(matr)ices$", "$1ix");
			singulars.put("(quiz)zes$", "$1");
			singulars.put("(database)s$", "$1");
		}

		public static String pluralize(String word) {
			for (Map.Entry<String, String> entry : plurals.entrySet()) {
				String rule = entry.getKey();
				String replacement = entry.getValue();
				if (word.matches(rule)) {
					return word.replaceAll(rule, replacement);
				}
			}
			return word;
		}

		public static String depluralize(String word) {
			for (Map.Entry<String, String> entry : singulars.entrySet()) {
				String rule = entry.getKey();
				String replacement = entry.getValue();
				if (word.matches(rule)) {
					return word.replaceAll(rule, replacement);
				}
			}
			return word;
		}
	}

	private String toCamelCase(String input) {
		return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, input);
	}
}
