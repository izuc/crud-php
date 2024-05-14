<?php

namespace App\Services;

use App\Models\User;
use Firebase\JWT\JWT;
use Psr\Container\ContainerInterface;

class AuthService
{
    private $container;

    public function __construct(ContainerInterface $container)
    {
        $this->container = $container;
    }

    public function registerUser($username, $email, $password, $alias)
    {
        try {
            $user = new User();
            $user->email_address = $email;
            $user->password = password_hash($password, PASSWORD_DEFAULT);
            $user->first_name = $username;
            $user->alias = $alias;
            $user->save();
            return $user;
        } catch (\Exception $e) {
            throw new \Exception('Failed to register user: ' . $e->getMessage());
        }
    }

    public function loginUser($email, $password)
	{
		try {
			$user = User::where('email_address', $email)->first();
			if (!$user || !password_verify($password, $user->password)) {
				throw new \Exception('Invalid credentials');
			}

			return [
				'token' => $this->generateToken($user),
				'user' => $user
			];
		} catch (\Exception $e) {
			throw new \Exception('Failed to login user: ' . $e->getMessage());
		}
	}

    private function generateToken(User $user)
    {
        $payload = [
            'user_id' => $user->user_id,
            'email' => $user->email_address,
            'exp' => time() + (60 * 60 * 24), // Token expires in 24 hours
        ];

        $secretKey = $this->container->get('config')['jwt'];
        return JWT::encode($payload, $secretKey, 'HS256');
    }
}
