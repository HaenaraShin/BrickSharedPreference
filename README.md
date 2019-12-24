BrickSharedPreferences
===================================

![BrickSharedPreferences](brick_title.png)

🔒 EncryptedSharedPreferences Migration Support Library For Android.

# Introduce

- Simple way to keep your SharedPreferences safe and secure.

- This android library support migration legacy SharedPreferences to EncryptedSharedPreferences.

# How To Use

- You may have used SharedPreferences like follow.

```
mSharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE)
```
- To migrate it to EncryptedSharedPreferences, change codes like follow.  

```
mSharedPreferences = getBrickSharedPreferences(fileName, Context.MODE_PRIVATE)
```

- When you encrypt existing plain-text data and get rid of legacy plain-text data, use follow method.

```
BrickSharedPreferences.migrateEncryptedSharedPreferences()
```

- and all is done! 🎉


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
