<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class User extends Model
{
    protected $table = 'users';
    protected $primaryKey = 'user_id';

    protected $fillable = [
        'email_address',
        'password',
        'first_name',
        'alias',
    ];

    protected $hidden = [
        'password',
    ];
}