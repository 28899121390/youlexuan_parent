//用户表控制层
app.controller('userController', function ($scope, $controller, userService) {
    $scope.showName = function () {
        userService.showLoginName().success(function (response) {
            $scope.loginName = response.UserName;
        })

    }
});	