<?php

namespace App\Controllers;

use App\Services\AuthService;
use Psr\Http\Message\ResponseInterface as Response;
use Psr\Http\Message\ServerRequestInterface as Request;

class AuthController
{
    private $authService;

    public function __construct(AuthService $authService)
    {
        $this->authService = $authService;
    }

    public function register(Request $request, Response $response): Response
    {
        try {
            $userData = $request->getParsedBody();
            $username = $userData['username'];
            $email = $userData['email'];
            $password = $userData['password'];
            $alias = $userData['alias'];

            $user = $this->authService->registerUser($username, $email, $password, $alias);

            $response->getBody()->write(json_encode(['message' => 'User registered successfully', 'user' => $user]));
            return $response->withHeader('Content-Type', 'application/json')->withStatus(201);
        } catch (\Exception $e) {
            $response->getBody()->write(json_encode(['error' => 'Registration failed: ' . $e->getMessage()]));
            return $response->withHeader('Content-Type', 'application/json')->withStatus(500);
        }
    }

    public function login(Request $request, Response $response): Response
	{
		try {
			$credentials = $request->getParsedBody();
			$email = $credentials['email'];
			$password = $credentials['password'];

			$user = $this->authService->loginUser($email, $password);

			$response->getBody()->write(json_encode(['message' => 'Login successful', 'token' => $user['token'], 'user' => $user['user']]));
			return $response->withHeader('Content-Type', 'application/json')->withStatus(200);
		} catch (\Exception $e) {
			$response->getBody()->write(json_encode(['error' => 'Login failed: ' . $e->getMessage()]));
			return $response->withHeader('Content-Type', 'application/json')->withStatus(401);
		}
	}
}
