cd /data/abs/lvs/bin
pkill -9 -ef lvs

mkdir /data/abs/lvs/deploy/history/$(date +%F)
mkdir /data/abs/lvs/deploy/history/$(date +%F)/${BUILD_NUMBER}

cp -rf /data/abs/lvs/deploy/*.jar /data/abs/lvs/deploy/history/$(date +%F)/${BUILD_NUMBER}

rm -rf  /data/abs/lvs/target/backup/*

mv -f /data/abs/lvs/target/*.jar /data/abs/lvs/target/backup/
mv -f /data/abs/lvs/deploy/*.jar /data/abs/lvs/target/

./run_script_wm.sh start