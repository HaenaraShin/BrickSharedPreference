🧱 BrickSharedPreferences
===================================

![BrickSharedPreferences](brick_title.png)

🔒 EncryptedSharedPreferences Migration Support Library For Android.

# Introduce

Simple way to keep your SharedPreferences secure and safe.

Support migration legacy SharedPreferences to EncryptedSharedPreferences.

- **Require minSdk 19.**

# How To Use

## Sample Run 

![Sample run](brick_sample_run.gif)

Sample app shows how BrickSharedPreferences works.

## Hot it works

![How it works](how_it_works.PNG)

BrickSharedPreferences deletes the legacy SharedPreferences file, and copies and encrypts as a new file name start with  "brick_".

After migration, just use a BrickSharedPreferences instance instead of SharedPreferences. 


## How to Migrate

When you encrypt existing plain-text data and get rid of legacy plain-text data, follow this method:
```
BrickSharedPreferences.migrateEncryptedSharedPreferences()
```
And all is done! 🎉

## How to Access EncryptedSharedPreferences

After migration, use a BrickSharedPreferences instance instead of SharedPreferences.

All you have to do is changing codes like this:  

```
// Only difference is adding "Brick" when you get SharedPreferences. 
mSharedPreferences = getBrickSharedPreferences(fileName, Context.MODE_PRIVATE)
```
And all is done! 🎉


# Licences

```
MIT License

Copyright (c) 2019 Haenala Shin

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

```
