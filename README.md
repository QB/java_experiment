## 5/24(日) QB 申し送り事項

### ユーザ認証
* 結構いじりました。
* アカウント情報は`users.dat`に保存されます。1行ごとに`ユーザ名,パスワードのハッシュ値`という形式で保存しています。
* 登録できるユーザ名は半角英数と下線のみ。既存アカウントとのユーザ名の重複は不可。これを、アカウント新規登録時にチェックする機能もつけました。
* `users.dat`には、とりあえず次のアカウントの情報を突っ込んであります。必要であれば、動かすときに使ってみてください。
  * ユーザ名: qb1、パスワード: pass1
  * ユーザ名: qb2、パスワード: pass2
  * ユーザ名: qb3、パスワード: pass3
* `Users`クラスの`authenticate()`メソッドは、`authenticate()`メソッドの内部で`name`と`password`を訊く形にして、引数は廃止してみました。差し支えがあれば元に戻します。
* アカウントを削除する`deleteUser()`メソッドは、`users.dat`を一度削除してから、削除されたアカウントを抜きにして他のユーザの認証情報を書き込み直す、という暴挙に出ることで実装を成し遂げました。
* パスワード入力時に、パスワードを非表示にする方法は、`System.console()`を使う方法であれば可能みたいです。本体に組み込むのは挫折しましたが、参考までに`PassSample.java`も載せておきます。

### 接続時の振る舞い
* いきなりアカウント新規作成やログインを求めるのではなく、まず「どうしたいか」を尋ねる対話型のメニューを作りました。


