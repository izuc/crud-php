<?php

namespace App\Services;

use App\Models\User;

class UserService
{
    public function getUserById($userId)
    {
        try {
            $user = User::findOrFail($userId);
            return $user;
        } catch (\Exception $e) {
            throw new \Exception('User not found: ' . $e->getMessage());
        }
    }

    public function updateUser($userId, $data)
    {
        $user = User::find($userId);
        if (!$user) {
            throw new \Exception('User not found');
        }

        $user->update($data);
        return $user;
    }
}
