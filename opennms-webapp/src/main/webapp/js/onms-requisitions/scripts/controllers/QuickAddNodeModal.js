/**
* @author Alejandro Galue <agalue@opennms.org>
* @copyright 2014 The OpenNMS Group, Inc.
*/

(function() {

  'use strict';

  angular.module('onms-requisitions')

  /**
  * @ngdoc controller
  * @name QuickAddNodeModalController
  * @module onms-requisitions
  *
  * @requires $controller Angular controller
  * @requires $scope Angular local scope
  * @requires $uibModalInstance Angular UI modal instance
  * @requires foreignSources The list of available requisitions (a.k.a. foreign source)
  * @requires RequisitionsService The requisitions service
  * @requires growl The growl plugin for instant notifications
  *
  * @description The controller for manage the modal dialog for quick add a node to an existing requisition.
  */
  .controller('QuickAddNodeModalController', ['$controller', '$scope', '$uibModalInstance', 'foreignSources', 'RequisitionsService', 'growl', function($controller, $scope, $uibModalInstance, foreignSources, RequisitionsService, growl) {

    /**
    * @description Provision the current node and close the modal operation
    *
    * @name QuickAddNodeModalController:modalProvision
    * @ngdoc method
    * @methodOf QuickAddNodeModalController
    */
    $scope.modalProvision = function() {
      $scope.provision();
      $uibModalInstance.close($scope.node);
    };

    /**
    * @description Cancels current modal operation
    *
    * @name QuickAddNodeModalController:modalCancel
    * @ngdoc method
    * @methodOf QuickAddNodeModalController
    */
    $scope.modalCancel = function() {
      $uibModalInstance.dismiss('cancel');
    };

    // Extending QuickAddNodeController
    angular.extend(this, $controller('QuickAddNodeController', {
      $scope: $scope,
      foreignSources: foreignSources,
      RequisitionsService: RequisitionsService,
      growl: growl
    }));

  }]);

}());
