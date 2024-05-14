<?php

ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

use Psr\Http\Message\ResponseInterface as Response;
use Psr\Http\Message\ServerRequestInterface as Request;
use Selective\BasePath\BasePathMiddleware;
use DI\ContainerBuilder;
use DI\Bridge\Slim\Bridge as SlimAppFactory;
use Psr\Container\ContainerInterface;
use Slim\Factory\AppFactory;
use App\Middleware\AuthMiddleware;
use App\Services\UserService;

require __DIR__ . '/../vendor/autoload.php';

// Load environment variables from .env file
try {
    $dotenv = Dotenv\Dotenv::createImmutable(__DIR__ . '/..');
    $dotenv->safeLoad();
} catch (Exception $e) {
    echo 'Dotenv could not be initialized: ', $e->getMessage(), "\n";
    exit();
}

// Create Container using PHP-DI
$containerBuilder = new ContainerBuilder();

$containerBuilder->addDefinitions([
    'config' => [
        'name' => $_ENV['APP_NAME'],
        'env' => $_ENV['APP_ENV'],
        'debug' => $_ENV['APP_DEBUG'] ?: false,
        'url' => $_ENV['APP_URL'],
        'timezone' => 'UTC',
        'locale' => 'en',
        'fallback_locale' => 'en',
        'key' => $_ENV['APP_KEY'],
        'jwt' => $_ENV['JWT_SECRET_KEY'],
        'cipher' => 'AES-256-CBC',

        // Database Configuration
        'database' => [
            'driver' => $_ENV['DB_DRIVER'],
            'host' => $_ENV['DB_HOST'],
            'port' => $_ENV['DB_PORT'],
            'database' => $_ENV['DB_DATABASE'],
            'username' => $_ENV['DB_USERNAME'],
            'password' => $_ENV['DB_PASSWORD'],
            'charset' => 'utf8mb4',
            'collation' => 'utf8mb4_unicode_ci',
            'prefix' => '',
        ],
    ],

    // Register the UserService for dependency injection
    UserService::class => \DI\autowire(UserService::class),
]);

// Register other dependencies
$containerBuilder->addDefinitions([
    'App\Services\AuthService' => function (ContainerInterface $container) {
        return new \App\Services\AuthService($container);
    },
]);

$container = $containerBuilder->build();
AppFactory::setContainer($container);
$app = AppFactory::create();

// Add the BasePathMiddleware
$app->add(new BasePathMiddleware($app));

// Create Eloquent Capsule Manager
$capsule = new \Illuminate\Database\Capsule\Manager;
$capsule->addConnection($container->get('config')['database']);
$capsule->setAsGlobal();
$capsule->bootEloquent();

// Add error middleware
$errorMiddleware = $app->addErrorMiddleware(true, true, true);

// Register routes
(require __DIR__ . '/../routes/api.php')($app);

// Add body parsing middleware
$app->addBodyParsingMiddleware();

// Add CORS middleware
$app->add(function (Request $request, \Psr\Http\Server\RequestHandlerInterface $handler) {
    $response = $handler->handle($request);
    return $response
        ->withHeader('Access-Control-Allow-Origin', '*')
        ->withHeader('Access-Control-Allow-Headers', 'X-Requested-With, Content-Type, Accept, Origin, Authorization')
        ->withHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, PATCH, OPTIONS');
});

// Run the app
$app->run();
