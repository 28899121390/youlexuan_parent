app.controller("searchController2",function ($scope,searchService2) {

    $scope.search=function () {
        searchService2.search($scope.searchMap).success(function (response) {
             $scope.result=response;
        })
    }
});